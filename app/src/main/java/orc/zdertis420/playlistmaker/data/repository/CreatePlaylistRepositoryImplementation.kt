package orc.zdertis420.playlistmaker.data.repository

import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.domain.repository.CreatePlaylistRepository

class CreatePlaylistRepositoryImplementation(private val dataBase: DataBase) : CreatePlaylistRepository {
    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        dataBase.getPlaylistDao().insertPlaylist(
            PlaylistDBEntity(
                name = name,
                description = description,
                coverImagePath = imagePath
            )
        )
    }
}