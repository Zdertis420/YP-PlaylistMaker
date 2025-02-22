package orc.zdertis420.playlistmaker

import orc.zdertis420.playlistmaker.data.network.RetrofitNetworkClient
import orc.zdertis420.playlistmaker.data.repository.TrackRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.api.TrackInteractor
import orc.zdertis420.playlistmaker.domain.api.TrackRepository
import orc.zdertis420.playlistmaker.domain.implementations.TrackInteractorImplementation

object Creator {
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImplementation(getTrackRepository())
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImplementation(RetrofitNetworkClient())
    }
}