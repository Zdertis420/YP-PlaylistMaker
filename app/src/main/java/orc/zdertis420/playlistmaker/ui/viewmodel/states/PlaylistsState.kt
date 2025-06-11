package orc.zdertis420.playlistmaker.ui.viewmodel.states

import orc.zdertis420.playlistmaker.domain.entities.Playlist

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Playlists(val playlists: List<Playlist>) : PlaylistsState()
    object Error : PlaylistsState()
}