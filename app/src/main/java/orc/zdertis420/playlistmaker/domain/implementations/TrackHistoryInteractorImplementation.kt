package orc.zdertis420.playlistmaker.domain.implementations

import orc.zdertis420.playlistmaker.domain.api.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.api.TrackHistoryRepository
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
}