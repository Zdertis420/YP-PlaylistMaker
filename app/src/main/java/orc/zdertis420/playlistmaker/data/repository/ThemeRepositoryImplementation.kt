package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import orc.zdertis420.playlistmaker.domain.repository.ThemeRepository
import androidx.core.content.edit

class ThemeRepositoryImplementation(private val context: Context) : ThemeRepository {

    private val themePreference by lazy {
        context.getSharedPreferences("THEME_SETTING", MODE_PRIVATE)
    }

    override fun switchTheme(isDarkModeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun saveTheme(theme: Boolean) {
        themePreference.edit {
            putBoolean("THEME", theme)
        }
    }

    override fun getTheme(): Boolean {
        return themePreference.getBoolean("THEME", false)
    }
}