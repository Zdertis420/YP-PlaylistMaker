package orc.zdertis420.playlistmaker

import android.app.Application

class App : Application() {

    var isDarkTheme = false
    private val themeInteractor = Creator.provideThemeInteractor()

    override fun onCreate() {
        super.onCreate()


        val preferences = getSharedPreferences("THEME_SETTING", MODE_PRIVATE)

        switchTheme(preferences.getBoolean("THEME", false))
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        themeInteractor.switchTheme(isDarkThemeEnabled)
    }
}