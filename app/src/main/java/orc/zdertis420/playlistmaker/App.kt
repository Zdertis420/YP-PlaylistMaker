package orc.zdertis420.playlistmaker

import android.app.Application
import orc.zdertis420.playlistmaker.di.database
import orc.zdertis420.playlistmaker.di.liked
import orc.zdertis420.playlistmaker.di.player
import orc.zdertis420.playlistmaker.di.playlists
import orc.zdertis420.playlistmaker.di.search
import orc.zdertis420.playlistmaker.di.settings
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    var isDarkTheme = false
    val themeInteractor by inject<ThemeInteractor>()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                player,
                search,
                settings,
                liked,
                playlists,
                database
            )
        }

        switchTheme(themeInteractor.getTheme())
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        themeInteractor.switchTheme(isDarkThemeEnabled)
    }
}