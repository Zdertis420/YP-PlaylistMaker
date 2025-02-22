package orc.zdertis420.playlistmaker.domain.implementations

import orc.zdertis420.playlistmaker.domain.api.TrackInteractor
import orc.zdertis420.playlistmaker.domain.api.TrackRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackInteractorImplementation(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun browseTracks(expression: String, callback: (Result<List<Track>>) -> Unit) {
            trackRepository.browseTracks(expression, callback)
    }
}