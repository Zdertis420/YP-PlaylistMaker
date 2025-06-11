package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.data.mapper.toPlaylist
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackLikedInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val savedStateHandle: SavedStateHandle,
    private val trackLikedInteractor: TrackLikedInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Preparing)
    val playerStateFlow: StateFlow<PlayerState> = _playerStateFlow.asStateFlow()

    private val _likeStateFlow = MutableStateFlow(false)
    val likeStateFlow: StateFlow<Boolean> = _likeStateFlow.asStateFlow()

    private val _playlistStateFlow = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val playlistStateFlow: StateFlow<PlaylistsState> = _playlistStateFlow.asStateFlow()

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
            _playerStateFlow.value = PlayerState.Error("No preview provided")
            Log.e("ERROR", "no preview")
            return
        }


        playerInteractor.prepare(
            track!!.previewUrl,
            onPrepared = { _playerStateFlow.value = PlayerState.Prepared },
            onCompleted = { _playerStateFlow.value = PlayerState.Prepared }
        )


//        Log.d("PLAYER STATE", playerStateLiveData.value.toString())
    }

    fun updatePlaybackTime() {
        val elapsedTime = playerInteractor.getCurrentPosition()
        val remainingTime = MAX_DURATION - elapsedTime
        _playerStateFlow.value = PlayerState.Play(elapsedTime, remainingTime)
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pause()
        } else {
            start()
        }
    }

    fun start() {
        if (playerStateFlow.value == PlayerState.Prepared || playerStateFlow.value == PlayerState.Pause) {
            playerInteractor.start()
            _playerStateFlow.value = PlayerState.Play(0, MAX_DURATION)

            updateTimeJob?.cancel()
            updateTimeJob = viewModelScope.launch(Dispatchers.Main) {
                while (true) {
                    updatePlaybackTime()
                    delay(DELAY)
                }
            }
        }

        Log.d("PLAYER STATE (VM)", playerStateFlow.value.toString())
    }

    fun pause() {
        playerInteractor.pause()
        _playerStateFlow.value = PlayerState.Pause

        updateTimeJob?.cancel()

        Log.d("PLAYER STATE (VM)", playerStateFlow.value.toString())
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch {
            if (_likeStateFlow.value) {
                trackLikedInteractor.unlikeTrack(track)
            } else {
                trackLikedInteractor.likeTrack(track)
            }
        }

        Log.d("TRACK", "Like toggled for ${track.trackName}: ${!track.isLiked}. VM")
    }

    fun observeLiked() {
        viewModelScope.launch {
            trackLikedInteractor.getLikedTracks().collectLatest { liked ->
                val isLiked = liked.any { it.trackId == track!!.trackId }
                _likeStateFlow.value = isLiked
                track!!.isLiked = isLiked
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                playlistInteractor.getPlaylists().collect { playlists ->
                    if (playlists.isEmpty()) {
                        _playlistStateFlow.value = PlaylistsState.Empty
                    } else {
                        _playlistStateFlow.value = PlaylistsState.Playlists(playlists.map { it.toPlaylist() })
                    }
                }
            } catch (_: Exception) {
                _playlistStateFlow.value = PlaylistsState.Error
            }
        }
    }

    fun addToPlaylist(playlistId: Long, track: Track) {
        viewModelScope.launch {
            try {
                playlistInteractor.addTrackToPlaylist(playlistId, track)
            } catch (e: Exception) {
                Log.d("PLAYLIST", "Error occurred while adding to playlist\n$e")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        updateTimeJob?.cancel()
        playerInteractor.release()
    }

    fun onActivityDestroyed() {
        Log.d("PLAYER", playerStateFlow.value.toString())
        updateTimeJob?.cancel()
        playerInteractor.release()
    }
}