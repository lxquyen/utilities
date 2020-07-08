package com.steve.utilities.domain.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RemoteControlClient
import android.media.session.PlaybackState
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.WIFI_MODE_FULL
import android.net.wifi.WifiManager.WifiLock
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import com.steve.utilities.R
import com.steve.utilities.presentation.media.*
import timber.log.Timber

/**
 * https://tool.oschina.net/uploads/apidocs/android/resources/samples/RandomMusicPlayer/src/com/example/android/musicplayer/MusicService.html
 */
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MusicFocusable, PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {
    companion object {
        const val ACTION_TOGGLE_PLAYBACK = "com.steve.utilities.action.TOGGLE_PLAYBACK"
        const val ACTION_PLAY = "com.steve.utilities.action.PLAY"
        const val ACTION_PAUSE = "com.steve.utilities.action.PAUSE"
        const val ACTION_STOP = "com.steve.utilities.action.STOP"
        const val ACTION_SKIP = "com.steve.utilities.action.SKIP"
        const val ACTION_REWIND = "com.steve.utilities.action.REWIND"
        const val ACTION_URL = "com.steve.utilities.action.URL"
        const val DUCK_VOLUME = 0.1f
        const val NOTIFICATION_ID = 1
    }


    var player: MediaPlayer? = null
    var audioFocusHelper: AudioFocusHelper? = null

    enum class State {
        Retrieving,         // the MediaRetriever is retrieving music
        Stopped,            // media player is stopped and not prepared to play
        Preparing,          // media player is preparing...
        Playing,            // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused              //playback paused (media player ready!)
    }

    var state = State.Retrieving


    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not
    private var startPlayingAfterRetrieve = false

    // if startPlayingAfterRetrieve is true, this variable indicates the URL that we should
    // start playing when we are ready. If null, we should play a random song from the device
    var whatToPlayAfterRetrieve: Uri? = null

    enum class PauseReason {
        UserRequest,        // paused by user request
        FocusLoss           // paused because of audio focus loss
    }

    var pauseReason = PauseReason.UserRequest

    enum class AudioFocus {
        NoFocusNoDuck,      // we don't have audio focus, and can't duck
        NoFocusCanDuck,     // we don't have focus, but can play at a low volume ("ducking")
        Focused             // we have full audio focus
    }

    var audioFocus = AudioFocus.NoFocusCanDuck

    var songTitle = ""

    // whether the song we are playing is streaming from the network
    var isStreaming = false

    lateinit var wifiLock: WifiLock

    // Our instance of our MusicRetriever, which handles scanning for media and
    // providing titles and URIs as we need
    lateinit var retriever: MusicRetriever

    // Our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available
    var remoteControlClientCompat: RemoteControlClientCompat? = null

    // Dummy album art we will pass to the remote control (if the APIs are available)
    lateinit var dummyAlbumArt: Bitmap

    // The component name of MusicIntentReceiver, for use with media button and remote control APIs
    lateinit var mediaButtonReceiverComponent: ComponentName

    lateinit var audioManager: AudioManager

    lateinit var notificationManager: NotificationManager

    var notification: Notification? = null

    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists
     */
    fun createMediaPlayerIfNeeded() {
        player?.reset()
        player = MediaPlayer()
            .apply {

                // Make sure the media player will acquire a wake-lock while playing. If we don't do
                // that, the CPU might go to sleep while the song is playing, causing playback to stop.
                //
                // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
                // permission in AndroidManifest.xml.
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

                // we want the media player to notify us when it's ready preparing, and when it's done
                // playing:
                setOnPreparedListener(this@MusicService)
                setOnCompletionListener(this@MusicService)
                setOnErrorListener(this@MusicService)
            }
    }

    override fun onCreate() {
        Timber.i("debug: Creating service")

        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .createWifiLock(WIFI_MODE_FULL, "myLock")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Create the retriever and start an asynchronous task that will prepare it.
        retriever = MusicRetriever(contentResolver)
        PrepareMusicRetrieverTask(retriever, this).execute()

        audioFocusHelper = AudioFocusHelper(applicationContext, this)
        dummyAlbumArt = BitmapFactory.decodeResource(resources, R.drawable.dummy_album_art)
        mediaButtonReceiverComponent = ComponentName(this, MusicIntentReceiver::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE_PLAYBACK -> processTogglePlaybackRequest()
            ACTION_PLAY -> processPlayRequest()
            ACTION_PAUSE -> processPauseRequest()
            ACTION_SKIP -> processSkipRequest()
            ACTION_STOP -> processStopRequest()
            ACTION_REWIND -> processRewindRequest()
            ACTION_URL -> processAddRequest(intent)
        }
        return START_NOT_STICKY // Means we started the service, but don't want it to restart in case it's killed
    }

    private fun processTogglePlaybackRequest() {
        if (state == State.Paused || state == State.Stopped) {
            processPlayRequest()
        } else {
            processPauseRequest()
        }
    }

    private fun processPlayRequest() {
        if (state == State.Retrieving) {
            // If we are still retrieving media, just set the flag to start playing when we're ready
            whatToPlayAfterRetrieve = null
            startPlayingAfterRetrieve = true
            return
        }

        tryToGetAudioFocus()

        //actually play the song

        if (state == State.Stopped) {
            playNextSong()
        } else if (state == State.Paused) {
            // If we're paused, just continue playback and restore the 'foreground service' state
            state = State.Playing
            setupAsForeground("$songTitle playing")
            configAndStartMediaPlayer()
        }

        // Tell any remote controls that our playback state is 'playing'.
        remoteControlClientCompat?.playbackState = PlaybackState.STATE_PLAYING
    }

    private fun processPauseRequest() {
        if (state == State.Retrieving) {
            startPlayingAfterRetrieve = false
            return
        }

        if (state == State.Playing) {
            state = State.Paused
            player?.pause()
            // while paused, we always retain the MediaPlayer
            // do not give up audio focus
            relaxResources(false)
        }

        remoteControlClientCompat?.playbackState = PlaybackState.STATE_PAUSED
    }

    private fun processSkipRequest() {
        if (state == State.Playing || state == State.Paused) {
            tryToGetAudioFocus()
            playNextSong()
        }
    }

    private fun processStopRequest(force: Boolean = false) {
        if (state == State.Playing || state == State.Paused || force) {
            state = State.Stopped

            relaxResources(true)
            giveUpAudioFocus()

            remoteControlClientCompat?.playbackState = PlaybackState.STATE_STOPPED

            stopSelf()
        }
    }

    private fun processRewindRequest() {
        if (state == State.Playing || state == State.Paused) {
            player?.seekTo(0)
        }
    }

    private fun giveUpAudioFocus() {
        if (audioFocus == AudioFocus.Focused && audioFocusHelper?.abandonFocus() == true) {
            audioFocus = AudioFocus.NoFocusNoDuck
        }
    }

    private fun configAndStartMediaPlayer() {
        if (audioFocus == AudioFocus.NoFocusNoDuck) {
            if (player?.isPlaying == true) {
                player?.pause()
            }
            return
        }

        if (audioFocus == AudioFocus.NoFocusCanDuck) {
            player?.setVolume(DUCK_VOLUME, DUCK_VOLUME)
            return
        }

        player?.setVolume(1.0f, 1.0f)

        if (player?.isPlaying == false) player?.start()

    }

    private fun processAddRequest(intent: Intent) {
        if (state == State.Retrieving) {
            whatToPlayAfterRetrieve = intent.data
            startPlayingAfterRetrieve = true
            return
        }

        if (state == State.Playing || state == State.Paused || state == State.Stopped) {
            Timber.i("Playing from URL/path: ${intent.data?.toString()}")
            tryToGetAudioFocus()
            playNextSong(intent.data.toString())
        }
    }


    private fun tryToGetAudioFocus() {
        if (audioFocus != AudioFocus.Focused && audioFocusHelper?.requestFocus() == true) {
            audioFocus = AudioFocus.Focused
        }
    }

    private fun playNextSong(path: String? = null) {
        state = State.Stopped
        relaxResources(false)
        try {
            var playingItem: MusicRetriever.Item? = null
            path?.let {
                createMediaPlayerIfNeeded()
                player?.apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(it)
                    isStreaming = it.startsWith("http:") || it.startsWith("https:")
                    playingItem = MusicRetriever.Item(0, null, it, null, 0)
                }
            } ?: run {
                isStreaming = false
                playingItem = retriever.getRandomItem()


                if (playingItem == null) {
                    Toast.makeText(this@MusicService, "No available music to play. Place some music on your external storate" +
                            " device (e.g. your SD card) and try again.",
                        Toast.LENGTH_LONG).show()
                    return
                }

                createMediaPlayerIfNeeded()
                player?.apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(applicationContext, playingItem!!.uri)
                }
            }

            songTitle = playingItem!!.title
            state = State.Preparing
            setupAsForeground("$songTitle loading")

            MediaButtonHelper.registerMediaButtonEventReceiverCompat(audioManager, mediaButtonReceiverComponent)

            if (remoteControlClientCompat == null) {
                val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
                    .apply {
                        component = mediaButtonReceiverComponent
                    }

                remoteControlClientCompat = RemoteControlClientCompat(
                    PendingIntent.getBroadcast(this, 0, intent, 0))

                RemoteControlHelper.registerRemoteControlClient(audioManager, remoteControlClientCompat)
            }

            remoteControlClientCompat?.playbackState = PlaybackState.STATE_PLAYING

            remoteControlClientCompat?.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY or
            )
        }
    }

    private fun relaxResources(releaseMediaPlayer: Boolean) {
        stopForeground(true)

        if (releaseMediaPlayer && player != null) {
            player?.reset()
            player?.release()
            player = null
        }

        if (wifiLock.isHeld) wifiLock.release()
    }

    private fun setupAsForeground(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPrepared(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGainedAudioFocus() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLostAudioFocus(canDuck: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}