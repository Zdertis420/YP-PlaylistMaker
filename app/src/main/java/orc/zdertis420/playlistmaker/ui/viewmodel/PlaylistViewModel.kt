package orc.zdertis420.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist

    fun setPlaylist(playlist: Playlist) {
        this.playlist = playlist
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlist.id, track.trackId)
        }
    }
}