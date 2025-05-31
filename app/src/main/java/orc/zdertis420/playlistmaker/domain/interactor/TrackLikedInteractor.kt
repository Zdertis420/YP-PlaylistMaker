package orc.zdertis420.playlistmaker.domain.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackLikedInteractor {
    suspend fun likeTrack(track: Track)

    suspend fun unlikeTrack(track: Track)

    fun getLikedTracks(): Flow<List<Track>>
}