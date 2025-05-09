package orc.zdertis420.playlistmaker.domain.repository

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.domain.entities.Track

interface TrackRepository {
    fun browseTracks(expression: String): Flow<Result<List<Track>>>
}