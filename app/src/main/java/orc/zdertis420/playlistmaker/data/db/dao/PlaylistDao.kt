package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistContent
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistCross
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBPlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistDBEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackDBPlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistCross(crossRef: PlaylistCross)

    @Transaction
    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylistsWithTracks(): List<PlaylistContent>

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistContent

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistDBEntity>

    @Transaction
    @Query("SELECT trackId FROM playlist_cross WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT trackId FROM playlist_cross WHERE trackId = :trackId")
    suspend fun getAllRefsForTrack(trackId: Long): List<Long>

    @Query("DELETE FROM playlist_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_cross WHERE playlistId = :playlistId")
    suspend fun deleteAllCrossForPlaylist(playlistId: Long)

    @Query("DELETE FROM tracks_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Transaction
    suspend fun deletePlaylistWithCleanup(playlistId: Long) {
        val trackIds = getTrackIdsForPlaylist(playlistId)

        deleteAllCrossForPlaylist(playlistId)

        for (trackId in trackIds) {
            val refs = getAllRefsForTrack(trackId)
            if (refs.isEmpty()) {
                deleteTrack(trackId)
            }
        }

        deletePlaylistById(playlistId)
    }

    @Transaction
    suspend fun insertTrackAndUpdateCount(track: TrackDBPlaylistEntity, playlistId: Long): Boolean {
        val existingRef = getPlaylistTrackCross(playlistId, track.trackId)

        if (existingRef == null) {
            insertTrack(track)
            insertPlaylistCross(PlaylistCross(playlistId, track.trackId))

            val playlist = getPlaylistById(playlistId)
            updatePlaylist(playlist.copy(amount = playlist.amount + 1))
            return true
        }
        return false
    }

    @Query("SELECT * FROM playlist_cross WHERE playlistId = :playlistId AND trackId = :trackId LIMIT 1")
    suspend fun getPlaylistTrackCross(playlistId: Long, trackId: Long): PlaylistCross?

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: Long): PlaylistDBEntity

    @Update
    suspend fun updatePlaylist(playlist: PlaylistDBEntity)
}