package orc.zdertis420.playlistmaker.domain.usecase

interface CreatePlaylistUseCase {
    suspend fun createPlaylist(name: String, description: String, imagePath: String)
}