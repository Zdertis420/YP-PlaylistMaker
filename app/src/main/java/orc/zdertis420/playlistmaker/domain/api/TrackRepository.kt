package orc.zdertis420.playlistmaker.domain.api

import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackRepository {
    fun browseTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
}