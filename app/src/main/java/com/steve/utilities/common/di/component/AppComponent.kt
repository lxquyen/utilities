package com.steve.utilities.common.di.component

import android.app.Application
import com.steve.utilities.common.di.module.AppModule
import com.steve.utilities.presentation.MainFragment
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

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun baseUrl(url: String): Builder

        fun build(): AppComponent
    }
}