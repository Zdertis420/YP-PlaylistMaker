package orc.zdertis420.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import orc.zdertis420.playlistmaker.data.db.dao.CachedDao
import orc.zdertis420.playlistmaker.data.db.dao.LikedDao
import orc.zdertis420.playlistmaker.data.db.dao.PlaylistsDao
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.CachedTrackDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity

@Database(version = 1, entities = [
    TrackDBEntity::class,
    PlaylistDBEntity::class,
    CachedTrackDBEntity::class,
    PlaylistTrackCrossRef::class
])
abstract class DataBase : RoomDatabase() {
    abstract fun getLikedDao(): LikedDao

    abstract fun getPlaylistDao(): PlaylistsDao

    abstract fun getCachedDao(): CachedDao
}