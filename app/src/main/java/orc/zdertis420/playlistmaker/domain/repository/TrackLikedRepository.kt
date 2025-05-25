package orc.zdertis420.playlistmaker.domain.repository

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackLikedRepository {
    fun likeTrack(trackId: Long)

    fun unlikeTrack(trackId: Long)

    fun getLiked(): Flow<List<Track>>
}