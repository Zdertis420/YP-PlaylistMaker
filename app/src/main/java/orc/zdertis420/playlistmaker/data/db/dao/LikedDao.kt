package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBEntity

@Dao
interface LikedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLiked(track: TrackDBEntity)

    @Query("DELETE FROM liked_table WHERE trackId = :trackId")
    suspend fun deleteLikedById(trackId: Long)

    @Query("SELECT * FROM liked_table")
    suspend fun getLikedTracks(): List<TrackDBEntity>

    @Query("SELECT trackId FROM liked_table")
    suspend fun getLikedIds(): List<Long>
}