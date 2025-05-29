package orc.zdertis420.playlistmaker.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackDto (
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isLiked: Boolean
) : Parcelable