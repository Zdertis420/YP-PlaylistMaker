package orc.zdertis420.playlistmaker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBPlaylistEntity
import orc.zdertis420.playlistmaker.domain.repository.PlaylistRepository

class PlaylistRepositoryImplementation(private val dataBase: DataBase) : PlaylistRepository {
    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        dataBase.getPlaylistDao().insertPlaylist(
            PlaylistDBEntity(
                name = name,
                description = description,
                imagePath = imagePath
            )
        )
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: TrackDBPlaylistEntity
    ): Boolean {
        return dataBase.getPlaylistDao().insertTrackAndUpdateCount(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        dataBase.getPlaylistDao().removeTrackFromPlaylist(playlistId, trackId)

        val playlist = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        dataBase.getPlaylistDao().updatePlaylist(playlist.copy(amount = playlist.amount-1))
    }

    override suspend fun removePlaylist(playlistId: Long) {
        dataBase.getPlaylistDao().deletePlaylistWithCleanup(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistDBEntity>> = flow {
        emit(dataBase.getPlaylistDao().getAllPlaylists())
    }
}