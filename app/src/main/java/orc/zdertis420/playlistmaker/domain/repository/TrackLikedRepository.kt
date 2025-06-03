package orc.zdertis420.playlistmaker.domain.repository

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackLikedRepository {
    suspend fun likeTrack(track: Track)

    suspend fun unlikeTrack(track: Track)

    fun getLiked(): Flow<List<Track>>
}