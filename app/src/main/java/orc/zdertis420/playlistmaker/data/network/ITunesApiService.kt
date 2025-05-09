package orc.zdertis420.playlistmaker.data.network

import orc.zdertis420.playlistmaker.data.dto.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?q=song")
    suspend fun browseTracks(@Query("term") text: String): TrackResponse
}