package com.steve.utilities.presentation.media

import android.media.MediaPlayer
import android.os.Handler
import timber.log.Timber

interface PlayController {
    var listener: PlayControllerListener?

    fun play(path: String? = null)
    fun pause()
    fun stop(force: Boolean)
    fun seekTo(progress: Int)
}

interface PlayControllerListener {
    fun onCurrentPositionChanged(progress: Int, max: Int)
}

enum class State {
    PLAYING, STOPPED, PAUSED
}

class PlayControllerImpl : PlayController, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private var state = State.STOPPED
    private var player: MediaPlayer? = null

    private val handle = Handler()

    private val runnable = object : Runnable {
        override fun run() {
            if (state == State.PLAYING) {

                listener?.onCurrentPositionChanged(
                    player?.currentPosition ?: 0,
                    player?.duration ?: 0)

                handle.postDelayed(this, 200)
            }
        }
    }

    private fun updatePosition() {
        handle.removeCallbacks(runnable)
        handle.post(runnable)
    }

    // ===================================== PlayController ========================================

    override var listener: PlayControllerListener? = null

    override fun play(path: String?) {
        if (state == State.STOPPED) {
            player?.reset()
            player = MediaPlayer()
                .apply {
                    setOnPreparedListener(this@PlayControllerImpl)
                    setOnCompletionListener(this@PlayControllerImpl)
                    setOnErrorListener(this@PlayControllerImpl)
                    setDataSource(path)
                    prepare()
                }
        } else if (state == State.PAUSED) {
            player?.start()
            updatePosition()
        }
    }

    override fun pause() {
        if (state == State.PLAYING) {
            state = State.PAUSED
            player?.pause()
        }
    }

    override fun stop(force: Boolean) {
        if (state == State.PLAYING || state == State.PAUSED || force) {
            state = State.STOPPED
        }
    }

    override fun seekTo(progress: Int) {
        player?.seekTo(progress)
    }

    /**
     * OnPreparedListener
     */
    override fun onPrepared(mp: MediaPlayer?) {
        state = State.PLAYING
        player?.start()
        updatePosition()
    }

    /**
     * OnCompletionListener
     */
    override fun onCompletion(mp: MediaPlayer?) {
        state = State.STOPPED
        listener?.onCurrentPositionChanged(player?.duration ?: 0, player?.duration ?: 0)
        player?.stop()
        handle.removeCallbacks(runnable)
    }


    /**
     * OnErrorListener
     */
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Timber.e("onError: $what - $extra")
        return true
    }


}
