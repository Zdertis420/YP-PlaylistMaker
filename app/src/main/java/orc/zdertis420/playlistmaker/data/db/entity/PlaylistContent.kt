package orc.zdertis420.playlistmaker.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistContent(
    @Embedded
    val playlist: PlaylistDBEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistCross::class)
    )
    val tracks: List<TrackDBPlaylistEntity>
)
