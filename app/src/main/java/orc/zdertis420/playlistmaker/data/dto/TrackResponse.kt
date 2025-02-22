package orc.zdertis420.playlistmaker.data.dto

data class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
