package orc.zdertis420.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistDBEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long? = null,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val amount: Int = 0
)
