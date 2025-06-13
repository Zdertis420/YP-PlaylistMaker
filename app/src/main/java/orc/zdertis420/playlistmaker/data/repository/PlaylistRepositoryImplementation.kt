package orc.zdertis420.playlistmaker.data.repository

import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.data.mapper.toCachedTrackDBEntity
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.repository.PlaylistRepository

class PlaylistRepositoryImplementation(private val dataBase: DataBase) : PlaylistRepository {
    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ) {
        Log.d("PLAYLIST", "Adding track ${track.trackName} to playlist with id $playlistId")

        dataBase.getCachedDao().insertCachedTrack(track.toDto().toCachedTrackDBEntity())

        dataBase.getPlaylistDao().insertPlaylistTrackCrossRef(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.trackId,
                timeAdded = System.currentTimeMillis()
            )
        )

        dataBase.getPlaylistDao().updatePlaylist(playlistId, System.currentTimeMillis())
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        dataBase.withTransaction {
            dataBase.getPlaylistDao().deleteTrackFromPlaylistCrossRef(playlistId, trackId)

            val hasRefs = dataBase.getPlaylistDao().hasReferencesToTrack(trackId)

            if (!hasRefs) {
                dataBase.getCachedDao().deleteCachedTrackById(trackId)
                Log.d("CLEANUP", "Track $trackId removed from cache as it has no more references")
            } else {
                Log.i("CLEANUP", "Track $trackId has other references")
            }
        }
    }

    override suspend fun removePlaylist(playlistId: Long) {
        dataBase.withTransaction {
            val tracks = dataBase.getPlaylistDao().getTrackIdsForPlaylist(playlistId)

            dataBase.getPlaylistDao().deletePlaylistById(playlistId)
            dataBase.getPlaylistDao().deletePlaylistCrossRef(playlistId)

            tracks.forEach { trackId ->
                val hasRefs = dataBase.getPlaylistDao().hasReferencesToTrack(trackId)
                if (!hasRefs) {
                    dataBase.getCachedDao().deleteCachedTrackById(trackId)
                    Log.d("CLEANUP", "Track $trackId removed from cache after playlist $playlistId deletion")
                }
            }
        }
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistWithTracks>> {
        return dataBase.getPlaylistDao().getAllPlaylistsInfo().map { playlists ->
            playlists.map { playlist ->
                PlaylistWithTracks(
                    playlist = playlist,
                    tracks = dataBase.getPlaylistDao().getTracksForPlaylistSorted(playlist.playlistId).first()
                )
            }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Flow<PlaylistWithTracks> {
        return dataBase.getPlaylistDao().getPlaylistInfo(playlistId)
            .combine(dataBase.getPlaylistDao().getTracksForPlaylistSorted(playlistId)) { playlist, tracks ->
                PlaylistWithTracks(
                    playlist = playlist,
                    tracks = tracks
                )
            }
    }
}