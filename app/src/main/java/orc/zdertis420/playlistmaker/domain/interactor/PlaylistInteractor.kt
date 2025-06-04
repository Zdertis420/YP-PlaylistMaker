package orc.zdertis420.playlistmaker.domain.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBPlaylistEntity

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, imagePath: String)

    suspend fun addTrackToPlaylist(playlistId: Long, track: TrackDBPlaylistEntity): Boolean

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun removePlaylist(playlistId: Long)

    suspend fun getPlaylists(): Flow<List<PlaylistDBEntity>>
}