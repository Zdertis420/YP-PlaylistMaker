package orc.zdertis420.playlistmaker.data.network

import orc.zdertis420.playlistmaker.data.NetworkClient
import orc.zdertis420.playlistmaker.data.dto.Response
import orc.zdertis420.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApiService = retrofit.create(ITunesApiService::class.java)

    override suspend fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val response = iTunesApiService.browseTracks(dto.expression)
                return response.apply { resultCode = 200 }
            } catch (httpExc: HttpException) {
                return Response().apply { resultCode = httpExc.code() }
            } catch (_: Exception) {
                return Response().apply { resultCode = -1 }
            }
        }

        return Response().apply { resultCode = 400 }
    }
}