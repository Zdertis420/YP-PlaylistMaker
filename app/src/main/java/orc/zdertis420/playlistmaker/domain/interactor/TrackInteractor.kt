package orc.zdertis420.playlistmaker.domain.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackInteractor {
    fun browseTracks(expression: String): Flow<Result<List<Track>>>
}