package orc.zdertis420.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PMApiService {
    @GET("/search?entity=song")
    fun browseTracks(@Query("term") text: String): Call<TrackResponse>
}