package orc.zdertis420.playlistmaker.domain.repository

interface CreatePlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, imagePath: String)
}