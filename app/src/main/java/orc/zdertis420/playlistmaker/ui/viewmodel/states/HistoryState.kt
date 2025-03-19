package orc.zdertis420.playlistmaker.ui.viewmodel.states

import orc.zdertis420.playlistmaker.domain.entities.Track

sealed class HistoryState {
    object Empty : HistoryState()
    data class Success(val tracksHistory : List<Track>) : HistoryState()
}