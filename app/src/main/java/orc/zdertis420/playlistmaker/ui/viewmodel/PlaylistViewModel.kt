package orc.zdertis420.playlistmaker.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.data.mapper.toPlaylist
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.domain.usecase.SharePlaylistUseCase

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharePlaylistUseCase: SharePlaylistUseCase
) : ViewModel() {

    private val _shareStateFlow = MutableStateFlow<Intent?>(null)
    val shareStateFlow: StateFlow<Intent?> = _shareStateFlow.asStateFlow()

    private val _playlistStateFlow = MutableStateFlow<Playlist?>(null)
    val playlistStateFlow: StateFlow<Playlist?> = _playlistStateFlow.asStateFlow()

    private lateinit var playlist: Playlist

    fun setPlaylist(playlist: Playlist) {
        this.playlist = playlist
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlist.id, track.trackId)
            playlist = playlist.copy(tracks = playlist.tracks.filter { it.trackId != track.trackId })
        }
    }

    fun sharePlaylist() {
        _shareStateFlow.value = sharePlaylistUseCase.sharePlaylist(playlist)
    }

    fun playlistShared() {
        _shareStateFlow.value = null
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.removePlaylist(playlist.id)
        }
    }

    fun updateUI() {
        viewModelScope.launch {
            try {
                playlistInteractor.getPlaylistById(playlist.id).collect { playlist ->
                    _playlistStateFlow.value = playlist.toPlaylist()
                }
            } catch (e: Exception) {
                Log.e("PLAYLIST", e.toString())
            }
        }
    }
}