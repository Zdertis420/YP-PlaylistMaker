package orc.zdertis420.playlistmaker.domain.api

interface ThemeInteractor {
    fun switchTheme(isDarkModeEnabled: Boolean)

    fun saveTheme(theme: Boolean)

    fun getTheme(): Boolean
}