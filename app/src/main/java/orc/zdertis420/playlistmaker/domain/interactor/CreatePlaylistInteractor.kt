package orc.zdertis420.playlistmaker.domain.interactor

interface CreatePlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, imagePath: String)

    suspend fun updatePlaylist(playlistId: Long, name: String, description: String?, coverImagePath: String?)
}