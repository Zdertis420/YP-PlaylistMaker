package orc.zdertis420.playlistmaker.domain.usecase

import android.content.Intent
import orc.zdertis420.playlistmaker.domain.entities.Playlist

interface SharePlaylistUseCase {
    fun sharePlaylist(playlist: Playlist): Intent
}