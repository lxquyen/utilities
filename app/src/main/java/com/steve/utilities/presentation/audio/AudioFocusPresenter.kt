package com.steve.utilities.presentation.audio

import android.content.Context
import android.media.*
import android.media.AudioManager.MODE_RINGTONE
import android.os.Build
import androidx.annotation.RequiresApi
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.observableTransformer
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AudioFocusPresenter @Inject constructor() : BasePresenter<AudioFocusView>() {
    private var audioManager: AudioManager? = null

    private val audioFocusRequest: AudioFocusRequest by lazy {
        return@lazy AudioFocusRequest
            .Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .build()
    }

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->

    }

    private var isSpeakerphoneOn = false
    private var isBluetoothScoOn = false
    private var mode = -1
    private var ringerMode = -1
    private var ringtone: Ringtone? = null

    fun setup(context: Context?) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(context, uri)
            .apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .build()
                isLooping = true
            }

        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        Observable
            .interval(200, TimeUnit.MILLISECONDS)
            .compose(observableTransformer())
            .doOnNext {
                val isSpeakerphoneOnN = audioManager?.isSpeakerphoneOn ?: false
                val isBluetoothScoOnN = audioManager?.isBluetoothScoOn ?: false
                val modeN = audioManager?.mode ?: -1
                val ringerModeN = audioManager?.ringerMode ?: -1

                var change = false
                if (isSpeakerphoneOn != isSpeakerphoneOnN
                    || isBluetoothScoOn != isBluetoothScoOnN
                    || mode != modeN
                    || ringerMode != ringerModeN
                ) {
                    change = true
                }

                if (change) {
                    isSpeakerphoneOn = isSpeakerphoneOnN
                    isBluetoothScoOn = isBluetoothScoOnN
                    mode = modeN
                    ringerMode = ringerModeN

                    Timber.i("isSpeakerphoneOn: ${audioManager?.isSpeakerphoneOn}")
                    Timber.i("isBluetoothScoOn: ${audioManager?.isBluetoothScoOn}")
                    Timber.i("mode: ${audioManager?.mode}")
                    Timber.i("ringerMode: ${audioManager?.ringerMode}")
                }

            }
            .subscribe()
            .addToCompositeDisposable(disposable)
    }

    fun play() {
        if (ringtone?.isPlaying == false) {
            ringtone?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        audioManager?.abandonAudioFocusRequest(audioFocusRequest)
    }
}