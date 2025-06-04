package orc.zdertis420.playlistmaker.domain.implementations.interactor

import kotlinx.coroutines.flow.Flow
import orc.zdertis420.playlistmaker.data.db.entity.PlaylistDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.TrackDBPlaylistEntity
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.domain.repository.PlaylistRepository

class PlaylistInteractorImplementation(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun createPlaylist(
        name: String,
        description: String,
        imagePath: String
    ) {
        playlistRepository.createPlaylist(name, description, imagePath)
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: TrackDBPlaylistEntity
    ): Boolean {
        return playlistRepository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun removePlaylist(playlistId: Long) {
        playlistRepository.removePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistDBEntity>> {
        return playlistRepository.getPlaylists()
    }
}