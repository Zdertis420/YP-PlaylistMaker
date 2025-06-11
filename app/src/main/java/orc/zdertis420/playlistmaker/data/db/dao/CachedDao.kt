package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.CachedTrackDBEntity

@Dao
interface CachedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedTrack(track: CachedTrackDBEntity)

    @Query("SELECT * FROM cached_tracks WHERE trackId = :trackId")
    suspend fun getCachedTrackById(trackId: Long): CachedTrackDBEntity?

    @Query("SELECT * FROM cached_tracks")
    fun getCachedTracks(): Flow<List<CachedTrackDBEntity>>

    @Query("DELETE FROM cached_tracks WHERE trackId = :trackId")
    suspend fun deleteCachedTrackById(trackId: Long)
}