package orc.zdertis420.playlistmaker.ui.viewmodel.states

import orc.zdertis420.playlistmaker.domain.entities.Track

sealed class LikedState {
    object Loading : LikedState()
    data class LikedTracks(val tracks: List<Track>) : LikedState()
    data class Error(val msg: String) : LikedState()
    object Empty : LikedState()
}