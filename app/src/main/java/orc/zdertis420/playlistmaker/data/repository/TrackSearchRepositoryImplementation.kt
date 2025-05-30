package orc.zdertis420.playlistmaker.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import orc.zdertis420.playlistmaker.data.NetworkClient
import orc.zdertis420.playlistmaker.data.dto.TrackResponse
import orc.zdertis420.playlistmaker.data.dto.TrackSearchRequest
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.repository.TrackSearchRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackSearchRepositoryImplementation(private val networkClient: NetworkClient) : TrackSearchRepository {
    override fun browseTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val requestDto = TrackSearchRequest(query)
            val response = networkClient.doRequest(requestDto)

            if (response.resultCode != 200) {
                emit(Result.failure(Throwable("Error code: ${response.resultCode}")))
                return@flow
            }

            val trackResponse = response as TrackResponse

            val tracks = trackResponse.results.mapNotNull { trackDto ->
                try {
                    trackDto.toTrack()
                } catch (_: NullPointerException) {
                    Log.d("NULL FOUND", "SKIP THAT MF")
                    null
                }
            }

            emit(Result.success(tracks))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}