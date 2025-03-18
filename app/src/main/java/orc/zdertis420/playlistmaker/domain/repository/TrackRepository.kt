package orc.zdertis420.playlistmaker.domain.repository

import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackRepository {
    fun browseTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
}