package orc.zdertis420.playlistmaker.data.repository

import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.domain.repository.CreatePlaylistRepository
import java.util.Calendar

class CreatePlaylistRepositoryImplementation(private val dataBase: DataBase) : CreatePlaylistRepository {
    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        val currentMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentMillis
        val currentYear = calendar.get(Calendar.YEAR)

        dataBase.getPlaylistDao().insertPlaylist(
            PlaylistDBEntity(
                name = name,
                description = description,
                coverImagePath = imagePath,
                year = currentYear
            )
        )
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String?,
        coverImagePath: String?
    ) {
        dataBase.getPlaylistDao().updatePlaylistDetails(
            playlistId = playlistId,
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            lastModified = System.currentTimeMillis()
        )
    }
}