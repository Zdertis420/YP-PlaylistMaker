package orc.zdertis420.playlistmaker.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.repository.TrackLikedRepository

class TrackLikedRepositoryImplementation(private val dataBase: DataBase) : TrackLikedRepository {
    override fun likeTrack(trackId: Long) {

    }

    override fun unlikeTrack(trackId: Long) {

    }

    override fun getLiked(): Flow<List<Track>> = flow {
        val likedTracks = dataBase.getLikedDao().getLikedTracks()
        emit(likedTracks.map { it.toTrack() })
    }
}