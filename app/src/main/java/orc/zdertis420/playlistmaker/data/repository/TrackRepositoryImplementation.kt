package orc.zdertis420.playlistmaker.data.repository

import android.util.Log
import orc.zdertis420.playlistmaker.data.NetworkClient
import orc.zdertis420.playlistmaker.data.dto.TrackResponse
import orc.zdertis420.playlistmaker.data.dto.TrackSearchRequest
import orc.zdertis420.playlistmaker.domain.api.TrackRepository
import orc.zdertis420.playlistmaker.domain.entities.Track
import kotlin.concurrent.thread

class TrackRepositoryImplementation(private val networkClient: NetworkClient) : TrackRepository {
    override fun browseTracks(expression: String, callback: (Result<List<Track>>) -> Unit) {
        thread {

            val response = networkClient.doRequest(TrackSearchRequest(expression))
            
            try {
                if (response.resultCode == 200) {
                    val trackResponse = response as TrackResponse
                    
                    if (trackResponse.results.isNotEmpty()) {
                        val nullSafeTracks = mutableListOf<Track>()

                        for (i in 0..<trackResponse.resultCount) {
                            val trackDto = trackResponse.results[i]

                            try {
                                nullSafeTracks.add(
                                    Track(
                                        trackName = trackDto.trackName,
                                        artistName = trackDto.artistName,
                                        trackTimeMillis = trackDto.trackTimeMillis,
                                        artworkUrl100 = trackDto.artworkUrl100,
                                        collectionName = trackDto.collectionName,
                                        releaseDate = trackDto.releaseDate,
                                        primaryGenreName = trackDto.primaryGenreName,
                                        country = trackDto.country,
                                        previewUrl = trackDto.previewUrl
                                    )
                                )
                            } catch (npe: NullPointerException) {
                                Log.d("NULL FOUND", "SKIP THAT MF")
                                continue
                            }


                            callback(Result.success(nullSafeTracks))
                        }
                    } else {
                        callback(Result.success(emptyList()))
                    }
                } else {
                    callback(Result.failure(Throwable("ERROR ${response.resultCode}")))
                }
            } catch (e: Exception) {
                callback(Result.failure(Throwable(e)))
            }
        }
    }
}