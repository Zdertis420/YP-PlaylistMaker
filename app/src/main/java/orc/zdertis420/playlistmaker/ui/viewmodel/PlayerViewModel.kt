package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
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
        Log.d("THREAD", Thread.currentThread().toString())

        if (track.previewUrl == "") {
            _playerStateLiveData.value = PlayerState.Error("No preview provided")
            Log.e("ERROR", "no preview")
            return
        }

        playerInteractor.prepare(
            track.previewUrl,
            onPrepared = { _playerStateLiveData.value = PlayerState.Prepared },
            onCompleted = { _playerStateLiveData.value = PlayerState.Prepared }
        )

        Log.d("PLAYER", playerStateLiveData.value.toString())
    }

    fun playbackControl() {
        if (playerStateLiveData.value == PlayerState.Play) {
            pause()
        } else {
            start()
        }
    }

    fun start() {
        if (playerStateLiveData.value == PlayerState.Prepared || playerStateLiveData.value == PlayerState.Pause) {
            playerInteractor.start()
            _playerStateLiveData.value = PlayerState.Play
        }

        Log.d("PLAYER", playerStateLiveData.value.toString())
    }

    fun pause() {
        playerInteractor.pause()
        _playerStateLiveData.value = PlayerState.Pause

        Log.d("PLAYER", playerStateLiveData.value.toString())
    }

    fun getCurrentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    fun onActivityDestroyed() {
        playerInteractor.release()
    }
}