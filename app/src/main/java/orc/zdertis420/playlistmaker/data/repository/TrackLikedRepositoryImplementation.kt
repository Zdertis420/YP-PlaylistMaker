package orc.zdertis420.playlistmaker.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.mapper.toDBEntity
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.repository.TrackLikedRepository

class TrackLikedRepositoryImplementation(private val dataBase: DataBase) : TrackLikedRepository {
    override suspend fun likeTrack(track: Track) {
        Log.d("TRACK", "Liked track ${track.trackName}")
        dataBase.getLikedDao().addLiked(track.toDto().toDBEntity())
    }

    override suspend fun unlikeTrack(track: Track) {
        Log.d("TRACK", "Unliked track ${track.trackName}")
        dataBase.getLikedDao().deleteLikedById(track.trackId)
    }

    override fun getLiked(): Flow<List<Track>> = flow {
        Log.d("TRACK", "Loading liked tracks. Repo")
        emit(dataBase.getLikedDao().getLikedTracks().map { it.toTrack() })
    }
}