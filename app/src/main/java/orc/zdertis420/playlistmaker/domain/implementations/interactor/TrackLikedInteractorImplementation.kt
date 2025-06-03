package orc.zdertis420.playlistmaker.domain.implementations.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.TrackLikedInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackLikedRepository

class TrackLikedInteractorImplementation(private val trackLikedRepository: TrackLikedRepository) : TrackLikedInteractor {
    override suspend fun likeTrack(track: Track) {
        trackLikedRepository.likeTrack(track)
    }

    override suspend fun unlikeTrack(track: Track) {
        trackLikedRepository.unlikeTrack(track)
    }

    override fun getLikedTracks(): Flow<List<Track>> {
        return trackLikedRepository.getLiked()
    }
}