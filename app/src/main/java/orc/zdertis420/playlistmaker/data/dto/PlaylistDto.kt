package orc.zdertis420.playlistmaker.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistDto(
    val id: Long,
    val name: String,
    val description: String? = "",
    val imagePath: String? = "",
    val tracks: List<TrackDto> = emptyList(),
    val year: Int
) : Parcelable
