package orc.zdertis420.playlistmaker

import android.app.Application
import orc.zdertis420.playlistmaker.di.appModule
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
            modules(appModule)
        }

        switchTheme(themeInteractor.getTheme())
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        isDarkTheme = isDarkThemeEnabled

        themeInteractor.switchTheme(isDarkThemeEnabled)
    }
}