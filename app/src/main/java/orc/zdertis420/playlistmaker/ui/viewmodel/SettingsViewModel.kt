package orc.zdertis420.playlistmaker.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
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

    private val _actionLiveData = MutableLiveData<Intent>()
    val actionLiveData: LiveData<Intent> get() = _actionLiveData

    fun toggleTheme() {
        val newTheme = !themeInteractor.getTheme()

        themeInteractor.switchTheme(newTheme)
        themeInteractor.saveTheme(newTheme)
    }

    fun shareApp() {
        _actionLiveData.postValue(shareAppUseCase.shareApp())
    }

    fun contactSupport() {
        _actionLiveData.postValue(contactSupportUseCase.contactSupport())
    }

    fun seeEula() {
        _actionLiveData.postValue(seeEulaUseCase.seeEula())
    }
}