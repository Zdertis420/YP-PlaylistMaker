package orc.zdertis420.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences("THEME_SETTING", MODE_PRIVATE)

        switchTheme(preferences.getBoolean("THEME", false))
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}