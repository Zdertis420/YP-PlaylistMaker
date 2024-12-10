package orc.zdertis420.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName")
    val trackName: String, // Название композиции
    @SerializedName("artistName")
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long, // Продолжительность трека
    @SerializedName("artworkUrl100")
    val artworkUrl100: String, // Ссылка на изображение обложки
)
