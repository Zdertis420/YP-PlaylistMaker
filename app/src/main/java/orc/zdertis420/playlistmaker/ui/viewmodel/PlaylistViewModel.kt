package orc.zdertis420.playlistmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {



    fun deleteTrackFromPlaylist(trackId: Long) {
        playlistInteractor.removeTrackFromPlaylist()
    }
}