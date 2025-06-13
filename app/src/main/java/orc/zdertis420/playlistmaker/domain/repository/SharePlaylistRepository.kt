package orc.zdertis420.playlistmaker.domain.repository

import android.content.Intent
import orc.zdertis420.playlistmaker.domain.entities.Playlist

interface SharePlaylistRepository {
    fun share(playlist: Playlist): Intent
}