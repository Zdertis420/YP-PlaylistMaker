package orc.zdertis420.playlistmaker.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    private val _playerStateLiveData = MutableLiveData<PlayerState>()
    val playerStateLiveData: LiveData<PlayerState> get() = _playerStateLiveData

    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updatePlaybackTime()
            mainThreadHandler.postDelayed(this, 1000)
        }
    }

    companion object {
        private const val DELAY = 1000L
        private const val MAX_DURATION = 30000L
    }

    fun prepare() {
        if (track.previewUrl == "") {
            _playerStateLiveData.value = PlayerState.Error("No preview provided")
            Log.e("ERROR", "no preview")
            return
        }

        playerInteractor.prepare(
            track.previewUrl,
            onPrepared = { _playerStateLiveData.postValue(PlayerState.Prepared) },
            onCompleted = { _playerStateLiveData.postValue(PlayerState.Prepared) }
        )

//        Log.d("PLAYER STATE", playerStateLiveData.value.toString())
    }

    private fun updatePlaybackTime() {
        val elapsedTime = playerInteractor.getCurrentPosition()
        val remainingTime = MAX_DURATION - elapsedTime
        _playerStateLiveData.postValue(PlayerState.Play(elapsedTime, remainingTime))
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pause()
        } else {
            start()
        }
    }

    fun start() {
        if (playerStateLiveData.value == PlayerState.Prepared || playerStateLiveData.value == PlayerState.Pause) {
            playerInteractor.start()
            _playerStateLiveData.postValue(PlayerState.Play(0, MAX_DURATION))
            mainThreadHandler.postDelayed(updateTimeRunnable, DELAY)
        }

        Log.d("PLAYER STATE (VM)", playerStateLiveData.value.toString())
    }

    fun pause() {
        playerInteractor.pause()
        _playerStateLiveData.postValue(PlayerState.Pause)

        mainThreadHandler.removeCallbacks(updateTimeRunnable)

        Log.d("PLAYER STATE (VM)", playerStateLiveData.value.toString())
    }

    fun onActivityDestroyed() {
        Log.d("PLAYER", playerStateLiveData.value.toString())
        playerInteractor.release()
    }
}