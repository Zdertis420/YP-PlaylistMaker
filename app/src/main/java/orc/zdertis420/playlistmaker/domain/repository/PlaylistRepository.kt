package orc.zdertis420.playlistmaker.domain.repository

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track

interface PlaylistRepository {
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun removePlaylist(playlistId: Long)

    suspend fun getPlaylists(): Flow<List<PlaylistWithTracks>>

    suspend fun getPlaylistById(playlistId: Long): Flow<PlaylistWithTracks>
}