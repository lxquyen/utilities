package com.steve.utilities.common.di.component

import android.app.Application
import com.steve.utilities.common.di.module.AppModule
import com.steve.utilities.presentation.MainFragment
import com.steve.utilities.presentation.audio.AudioFocusFragment
import com.steve.utilities.presentation.circularprogressbar.CircularProgressBarFragment
import com.steve.utilities.presentation.gridmanager.GridManagerFragment
import com.steve.utilities.presentation.ripple.RippleFragment
import com.steve.utilities.presentation.rxjava.RxJavaFragment
import com.steve.utilities.presentation.service.ServiceExampleFragment
import com.steve.utilities.presentation.sudoku.play.SudokuGameFragment
import com.steve.utilities.presentation.sudoku.rank.RankFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent {
    fun inject(mainFragment: MainFragment)
    fun inject(sudokuGameFragment: SudokuGameFragment)
    fun inject(rankFragment: RankFragment)
    fun inject(rxJavaFragment: RxJavaFragment)
    fun inject(audioFocusFragment: AudioFocusFragment)
    fun inject(serviceExampleFragment: ServiceExampleFragment)
    fun inject(circularProgressBarFragment: CircularProgressBarFragment)
    fun inject(gridManagerFragment: GridManagerFragment)
    fun inject(rippleFragment: RippleFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun baseUrl(url: String): Builder

        fun build(): AppComponent
    }
}