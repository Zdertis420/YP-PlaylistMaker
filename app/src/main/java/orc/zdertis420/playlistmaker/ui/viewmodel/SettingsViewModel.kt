package orc.zdertis420.playlistmaker.ui.viewmodel

import androidx.lifecycle.MutableLiveData
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

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled get() = _isDarkThemeEnabled

    init {
        _isDarkThemeEnabled.postValue(themeInteractor.getTheme())
    }

    fun toggleTheme() {
        val newTheme = !themeInteractor.getTheme()

        themeInteractor.switchTheme(newTheme)
        _isDarkThemeEnabled.postValue(newTheme)
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