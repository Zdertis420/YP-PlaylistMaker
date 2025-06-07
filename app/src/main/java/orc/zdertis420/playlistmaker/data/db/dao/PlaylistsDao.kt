package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistDBEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef)

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    @Query("SELECT COUNT(trackId) FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    fun getTrackCountForPlaylist(playlistId: Long): Flow<Int>

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylistCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Query("UPDATE playlists SET name = :name, description = :description, coverImagePath = :coverImagePath WHERE playlistId = :playlistId")
    suspend fun updatePlaylistDetails(playlistId: Long, name: String, description: String?, coverImagePath: String?)
}