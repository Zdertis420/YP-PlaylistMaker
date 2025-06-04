package orc.zdertis420.playlistmaker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.data.mapper.toCachedTrackDBEntity
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.domain.entities.Track
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
                coverImagePath = imagePath
            )
        )
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ) {
        dataBase.getPlaylistDao().insertPlaylistTrackCrossRef(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.trackId,
                timeAdded = System.currentTimeMillis()
            )
        )

        dataBase.getCachedDao().insertCachedTrack(track.toDto().toCachedTrackDBEntity())
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        dataBase.getPlaylistDao().deleteTrackFromPlaylistCrossRef(playlistId, trackId)
        dataBase.getCachedDao().deleteCachedTrackById(trackId)
    }

    override suspend fun removePlaylist(playlistId: Long) {
        dataBase.getPlaylistDao().deletePlaylistById(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistWithTracks>> {
        return dataBase.getPlaylistDao().getAllPlaylistsWithTracks()
    }

}