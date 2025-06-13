package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.CachedTrackDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistTrackCrossRef

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistDBEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    fun getPlaylistInfo(playlistId: Long): Flow<PlaylistDBEntity>

    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsInfo(): Flow<List<PlaylistDBEntity>>

    @Query(
        """
            SELECT cached_track.* 
            FROM cached_tracks AS cached_track 
            INNER JOIN playlist_track_cross_ref AS cross_ref ON cached_track.trackId = cross_ref.trackId
            WHERE cross_ref.playlistId = :playlistId
            ORDER BY cross_ref.timeAdded DESC
        """
    )
    fun getTracksForPlaylistSorted(playlistId: Long): Flow<List<CachedTrackDBEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM playlist_track_cross_ref WHERE trackId = :trackId LIMIT 1)")
    suspend fun hasReferencesToTrack(trackId: Long): Boolean

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylistCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deletePlaylistCrossRef(playlistId: Long)

    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("UPDATE playlists SET name = :name, description = :description, coverImagePath = :coverImagePath, lastModified = :lastModified WHERE playlistId = :playlistId")
    suspend fun updatePlaylistDetails(
        playlistId: Long,
        name: String,
        description: String?,
        coverImagePath: String?,
        lastModified: Long
    )

    @Query("UPDATE playlists SET lastModified = :lastModified WHERE playlistId = :playlistId")
    suspend fun updatePlaylist(playlistId: Long, lastModified: Long)
}