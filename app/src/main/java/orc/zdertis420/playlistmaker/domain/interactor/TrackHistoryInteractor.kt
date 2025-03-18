package orc.zdertis420.playlistmaker.domain.interactor

import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackHistoryInteractor {
    fun getTrackHistory(): MutableList<Track>

    fun saveTrackHistory(trackHistory: MutableList<Track>)

    fun clearTrackHistory()
}