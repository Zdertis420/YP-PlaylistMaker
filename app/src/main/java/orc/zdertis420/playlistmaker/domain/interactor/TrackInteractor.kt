package orc.zdertis420.playlistmaker.domain.interactor

import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackInteractor {
    fun browseTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
}