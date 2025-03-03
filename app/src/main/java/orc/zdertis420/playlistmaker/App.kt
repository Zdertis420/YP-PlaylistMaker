package orc.zdertis420.playlistmaker

import android.app.Application

class App : Application() {

    var isDarkTheme = false
    private val themeInteractor = Creator.provideThemeInteractor(this)

    override fun onCreate() {
        super.onCreate()

        switchTheme(themeInteractor.getTheme())
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        themeInteractor.switchTheme(isDarkThemeEnabled)
    }
}