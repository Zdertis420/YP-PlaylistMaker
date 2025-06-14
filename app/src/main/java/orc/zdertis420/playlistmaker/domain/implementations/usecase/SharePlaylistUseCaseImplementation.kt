package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Intent
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.repository.SharePlaylistRepository
import orc.zdertis420.playlistmaker.domain.usecase.SharePlaylistUseCase

class SharePlaylistUseCaseImplementation(private val sharePlaylistRepository: SharePlaylistRepository) :
    SharePlaylistUseCase {
    override fun sharePlaylist(playlist: Playlist): Intent {
        return sharePlaylistRepository.share(playlist)
    }
}