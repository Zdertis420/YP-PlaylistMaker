package orc.zdertis420.playlistmaker.data.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import orc.zdertis420.playlistmaker.data.db.entity.CachedTrackDBEntity

interface CachedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Если трек уже есть в кэше, обновляем его
    suspend fun insertCachedTrack(track: CachedTrackDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedTracks(tracks: List<CachedTrackDBEntity>) // Для пакетной вставки/обновления

    @Query("SELECT * FROM cached_tracks WHERE trackId = :trackId")
    suspend fun getCachedTrackById(trackId: Long): CachedTrackDBEntity?

    @Query("SELECT * FROM cached_tracks WHERE trackId IN (:trackIds)")
    suspend fun getCachedTracksByIds(trackIds: List<Long>): List<CachedTrackDBEntity>

    // Можно добавить метод для удаления старых треков из кэша по lastAccessedTime, если нужна стратегия очистки
    @Query("DELETE FROM cached_tracks WHERE trackId = :trackId")
    suspend fun deleteCachedTrackById(trackId: Long)
}