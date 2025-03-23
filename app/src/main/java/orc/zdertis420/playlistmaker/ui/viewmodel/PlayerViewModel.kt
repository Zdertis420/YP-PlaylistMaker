package orc.zdertis420.playlistmaker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    private val _playerStateLiveData = MutableLiveData<PlayerState>()
    val playerStateLiveData: LiveData<PlayerState> get() = _playerStateLiveData

    fun prepare() {
        if (track.previewUrl == "") {
            _playerStateLiveData.postValue(PlayerState.Error("No preview provided"))
            return
        }

        playerInteractor.prepare(
            track.previewUrl,
            onPrepared = { _playerStateLiveData.postValue(PlayerState.Prepared) },
            onCompleted = { _playerStateLiveData.postValue(PlayerState.Prepared) }
        )
    }

    fun start() {
        if (playerStateLiveData.value == PlayerState.Prepared || playerStateLiveData.value == PlayerState.Pause) {
            playerInteractor.start()
            _playerStateLiveData.postValue(PlayerState.Play)
        }
    }

    fun pause() {
        playerInteractor.pause()
        _playerStateLiveData.postValue(PlayerState.Pause)
    }

    fun getCurrentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    fun onActivityPaused() {
        if (playerStateLiveData.value == PlayerState.Play) {
            pause()
        }
    }

    fun onActivityDestroyed() {
        playerInteractor.release()
    }

}