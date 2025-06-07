package orc.zdertis420.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.data.mapper.toPlaylist
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    init {
        loadPlaylists()
    }

    private val _playlistStateFlow = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val playlistStateFlow: StateFlow<PlaylistsState> = _playlistStateFlow.asStateFlow()

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
            } catch (e: Exception) {
                _playlistStateFlow.value = PlaylistsState.Error
            }
        }
    }
}