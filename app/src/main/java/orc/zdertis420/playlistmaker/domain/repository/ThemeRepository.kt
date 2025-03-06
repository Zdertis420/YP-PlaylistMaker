package orc.zdertis420.playlistmaker.domain.repository

interface ThemeRepository {
    fun switchTheme(isDarkModeEnabled: Boolean)

    fun saveTheme(theme: Boolean)

    fun getTheme(): Boolean
}