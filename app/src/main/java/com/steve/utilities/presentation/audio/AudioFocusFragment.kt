package com.steve.utilities.presentation.audio

import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.rtp.AudioStream
import android.os.Build
import androidx.annotation.RequiresApi
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_audio_focus.*
import javax.inject.Inject

class AudioFocusFragment : BaseFragment<AudioFocusView, AudioFocusPresenter>(), AudioFocusView {

    @Inject
    lateinit var presenter: AudioFocusPresenter
    private var ringtone: Ringtone? = null

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<AudioFocusView>? {
        return presenter
    }

    override fun viewIF(): AudioFocusView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_audio_focus
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        presenter.setup(context)

        btnPlay.setOnClickListener {
            presenter.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ringtone?.stop()
    }
}
