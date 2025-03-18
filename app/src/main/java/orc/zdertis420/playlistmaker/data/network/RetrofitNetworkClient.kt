package orc.zdertis420.playlistmaker.data.network

import orc.zdertis420.playlistmaker.data.NetworkClient
import orc.zdertis420.playlistmaker.data.dto.Response
import orc.zdertis420.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApiService = retrofit.create(ITunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val response = iTunesApiService.browseTracks(dto.expression).execute()

            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        }

        return Response().apply { resultCode = 400 }
    }
}