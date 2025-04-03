package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.usecase.ContactSupportUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val shareAppUseCase: ShareAppUseCase,
    private val contactSupportUseCase: ContactSupportUseCase,
    private val seeEulaUseCase: SeeEulaUseCase
) : ViewModel() {

    init {

        Log.v("VIEW MODEL", "CREATED, INIT")
    }

    fun toggleTheme() {
        val newTheme = !themeInteractor.getTheme()

        themeInteractor.switchTheme(newTheme)
        themeInteractor.saveTheme(newTheme)

//        Log.d("THEME", newTheme.toString())
    }

    fun shareApp() {
        shareAppUseCase.shareApp()
    }

    fun contactSupport() {
        contactSupportUseCase.contactSupport()
    }

    fun seeEula() {
        seeEulaUseCase.seeEula()
    }
}