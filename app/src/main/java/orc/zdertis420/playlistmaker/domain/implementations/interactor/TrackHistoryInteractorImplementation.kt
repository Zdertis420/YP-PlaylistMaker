package orc.zdertis420.playlistmaker.domain.implementations.interactor

import orc.zdertis420.playlistmaker.domain.interactor.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackHistoryInteractorImplementation(
    private val trackHistoryRepository: TrackHistoryRepository
) : TrackHistoryInteractor {
    override fun getTrackHistory(): MutableList<Track> {
        return trackHistoryRepository.getTrackHistory()
    }

    override fun saveTrackHistory(trackHistory: MutableList<Track>) {
        trackHistoryRepository.saveTrackHistory(trackHistory)
    }

    override fun clearTrackHistory() {
        trackHistoryRepository.clearHistory()
    }
}