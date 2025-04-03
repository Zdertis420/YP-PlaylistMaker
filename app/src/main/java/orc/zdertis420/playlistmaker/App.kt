package orc.zdertis420.playlistmaker

import android.app.Application
import orc.zdertis420.playlistmaker.data.repository.ThemeRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.ThemeInteractorImplementation
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor

class App : Application() {

    var isDarkTheme = false
    private val themeInteractor = ThemeInteractorImplementation(ThemeRepositoryImplementation(this))

    override fun onCreate() {
        super.onCreate()

        switchTheme(themeInteractor.getTheme())
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        themeInteractor.switchTheme(isDarkThemeEnabled)
    }
}