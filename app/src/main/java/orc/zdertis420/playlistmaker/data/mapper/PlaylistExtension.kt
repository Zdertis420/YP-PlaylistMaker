package orc.zdertis420.playlistmaker.data.mapper

import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.domain.entities.Playlist

fun PlaylistWithTracks.toPlaylist() = Playlist(
    id = playlist.playlistId,
    name = playlist.name,
    description = playlist.description,
    imagePath = playlist.coverImagePath,
    tracks = tracks.map { it.toDto().toTrack() }
)