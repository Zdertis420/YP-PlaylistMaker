package orc.zdertis420.playlistmaker.domain.implementations.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackSearchRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackInteractorImplementation(private val trackSearchRepository: TrackSearchRepository) : TrackInteractor {

    override fun browseTracks(expression: String): Flow<Result<List<Track>>> {
        return trackSearchRepository.browseTracks(expression)
    }
}