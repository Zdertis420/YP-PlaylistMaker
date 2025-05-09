package orc.zdertis420.playlistmaker.domain.implementations.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackInteractorImplementation(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun browseTracks(expression: String): Flow<Result<List<Track>>> {
        return trackRepository.browseTracks(expression)
    }
}