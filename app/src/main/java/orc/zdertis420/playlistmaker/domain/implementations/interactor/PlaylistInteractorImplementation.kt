package orc.zdertis420.playlistmaker.domain.implementations.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.domain.repository.PlaylistRepository

class PlaylistInteractorImplementation(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ) {
        playlistRepository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun removePlaylist(playlistId: Long) {
        playlistRepository.removePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistWithTracks>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Flow<PlaylistWithTracks> {
        return playlistRepository.getPlaylistById(playlistId)
    }
}