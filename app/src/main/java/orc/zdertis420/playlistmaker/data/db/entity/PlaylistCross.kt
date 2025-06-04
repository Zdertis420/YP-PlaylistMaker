package orc.zdertis420.playlistmaker.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "playlist_cross",
    primaryKeys = ["playlistId", "trackId"]
)
data class PlaylistCross(
    val playlistId: Long,
    val trackId: Long
)
