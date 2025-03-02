package orc.zdertis420.playlistmaker.data.repository

import androidx.appcompat.app.AppCompatDelegate
import orc.zdertis420.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImplementation : ThemeRepository {
    override fun switchTheme(isDarkModeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}