package orc.zdertis420.playlistmaker.domain.implementations.usecase

import orc.zdertis420.playlistmaker.domain.repository.CreatePlaylistRepository
import orc.zdertis420.playlistmaker.domain.usecase.CreatePlaylistUseCase

class CreatePlaylistUseCaseImplementation(private val createPlaylistRepository: CreatePlaylistRepository)
    : CreatePlaylistUseCase {

    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        createPlaylistRepository.createPlaylist(name, description, imagePath)
    }
}