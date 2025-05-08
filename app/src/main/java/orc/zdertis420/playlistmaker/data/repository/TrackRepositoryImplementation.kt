package orc.zdertis420.playlistmaker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import orc.zdertis420.playlistmaker.data.NetworkClient
import orc.zdertis420.playlistmaker.data.dto.TrackResponse
import orc.zdertis420.playlistmaker.data.dto.TrackSearchRequest
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.repository.TrackRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackRepositoryImplementation(private val networkClient: NetworkClient) : TrackRepository {
    override fun browseTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val requestDto = TrackSearchRequest(query)
            val response = networkClient.doRequest(requestDto)

            if (response.resultCode == 200) {
                val itunesResponse = response as? TrackResponse
                val trackDto = itunesResponse?.results ?: emptyList()
                val domainTracks = trackDto.map { it.toTrack() }
                emit(Result.success(domainTracks))
            } else {
                emit(Result.failure(Throwable("Error code: ${response.resultCode}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}