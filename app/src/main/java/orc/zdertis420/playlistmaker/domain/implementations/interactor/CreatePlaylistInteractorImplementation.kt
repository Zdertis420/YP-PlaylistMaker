package orc.zdertis420.playlistmaker.domain.implementations.interactor

import android.util.Log
import orc.zdertis420.playlistmaker.domain.interactor.CreatePlaylistInteractor
import orc.zdertis420.playlistmaker.domain.repository.CreatePlaylistRepository

class CreatePlaylistInteractorImplementation(private val createPlaylistRepository: CreatePlaylistRepository)
    : CreatePlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        createPlaylistRepository.createPlaylist(name, description, imagePath)
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String?,
        coverImagePath: String?
    ) {
        Log.d("UPDATE", "playlist: $playlistId updating")

        createPlaylistRepository.updatePlaylist(
            playlistId = playlistId,
            name = name,
            description = description,
            coverImagePath = coverImagePath
        )
    }
}