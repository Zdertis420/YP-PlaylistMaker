package orc.zdertis420.playlistmaker.domain.implementations

import orc.zdertis420.playlistmaker.domain.api.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImplementation(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun switchTheme(isDarkModeEnabled: Boolean) {
        themeRepository.switchTheme(isDarkModeEnabled)
    }

    override fun saveTheme(theme: Boolean) {
        themeRepository.saveTheme(theme)
    }

    override fun getTheme(): Boolean {
        return themeRepository.getTheme()
    }
}