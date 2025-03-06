package orc.zdertis420.playlistmaker.domain.repository

import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackHistoryRepository {
    fun getTrackHistory(): MutableList<Track>

    fun saveTrackHistory(trackHistory: MutableList<Track>)

    fun clearHistory()
}