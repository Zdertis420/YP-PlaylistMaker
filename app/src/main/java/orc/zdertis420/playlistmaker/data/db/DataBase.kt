package orc.zdertis420.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import orc.zdertis420.playlistmaker.data.db.dao.LikedDao
import orc.zdertis420.playlistmaker.data.db.dao.PlaylistDao
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBEntity

@Database(version = 1, entities = [TrackDBEntity::class])
abstract class DataBase : RoomDatabase() {
    abstract fun getLikedDao(): LikedDao

    abstract fun getPlaylistDao(): PlaylistDao
}