package orc.zdertis420.playlistmaker.domain.entities

data class Playlist(
    val id: Long,
    val name: String,
    val description: String? = "",
    val imagePath: String? = "",
    val tracks: List<Track> = emptyList(),
    val year: Int
)
