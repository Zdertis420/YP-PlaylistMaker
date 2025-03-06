package orc.zdertis420.playlistmaker.domain.interactor

interface ThemeInteractor {
    fun switchTheme(isDarkModeEnabled: Boolean)

    fun saveTheme(theme: Boolean)

    fun getTheme(): Boolean
}