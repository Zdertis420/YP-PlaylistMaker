package orc.zdertis420.playlistmaker.ui.viewmodel.states

import orc.zdertis420.playlistmaker.domain.entities.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    data class History(val tracksHistory: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}