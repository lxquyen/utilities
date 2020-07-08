package com.steve.utilities.presentation.media

interface MusicFocusable {
    /**
     * Signals that audio focus was gained.
     */
    fun onGainedAudioFocus()

    /**
     * Signals that audio focus was lost.
     *
     * @param canDuck If true, audio can continue in "ducked" mode (low volume). Otherwise, all
     * audio must stop
     */
    fun onLostAudioFocus(canDuck: Boolean)
}