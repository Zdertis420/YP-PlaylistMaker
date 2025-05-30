package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackLikedInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val savedStateHandle: SavedStateHandle,
    private val trackLikedInteractor: TrackLikedInteractor
) : ViewModel() {

    private val _playerStateLiveData = MutableLiveData<PlayerState>()
    val playerStateLiveData: LiveData<PlayerState> get() = _playerStateLiveData

    private var updateTimeJob: Job? = null

    companion object {
        private const val DELAY = 300L
        private const val MAX_DURATION = 30000L
    }

    private var track: Track? = null

    init {
        savedStateHandle.get<TrackDto>("TRACK")?.let {
            track = it.toTrack()
        }
    }

    fun setTrack(track: Track) {
        this.track = track
        savedStateHandle["TRACK"] = track.toDto()
    }

    fun prepare() {
        if (track?.previewUrl == "") {
            _playerStateLiveData.value = PlayerState.Error("No preview provided")
            Log.e("ERROR", "no preview")
            return
        }


        playerInteractor.prepare(
            track!!.previewUrl,
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

            updateTimeJob?.cancel()
            updateTimeJob = viewModelScope.launch(Dispatchers.Main) {
                while (true) {
                    updatePlaybackTime()
                    delay(DELAY)
                }
            }
        }

        Log.d("PLAYER STATE (VM)", playerStateLiveData.value.toString())
    }

    fun pause() {
        playerInteractor.pause()
        _playerStateLiveData.postValue(PlayerState.Pause)

        updateTimeJob?.cancel()

        Log.d("PLAYER STATE (VM)", playerStateLiveData.value.toString())
    }

    fun toggleLike(track: Track) {
        Log.d("TRACK", "Like toggled for ${track.trackName}")

        viewModelScope.launch {
            if (track.isLiked) {
                track.isLiked = false
                trackLikedInteractor.unlikeTrack(track)
            } else {
                track.isLiked = true
                trackLikedInteractor.likeTrack(track)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        updateTimeJob?.cancel()
        playerInteractor.release()
    }

    fun onActivityDestroyed() {
        Log.d("PLAYER", playerStateLiveData.value.toString())
        updateTimeJob?.cancel()
        playerInteractor.release()
    }
}