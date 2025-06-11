package orc.zdertis420.playlistmaker.data.mapper

import orc.zdertis420.playlistmaker.data.db.entity.PlaylistWithTracks
import orc.zdertis420.playlistmaker.data.dto.PlaylistDto
import orc.zdertis420.playlistmaker.domain.entities.Playlist

fun PlaylistWithTracks.toPlaylist() = Playlist(
    id = playlist.playlistId,
    name = playlist.name,
    description = playlist.description,
    imagePath = playlist.coverImagePath,
    tracks = tracks.map { it.toDto().toTrack() },
    year = playlist.year
)

fun Playlist.toDto() = PlaylistDto(
    id = id,
    name = name,
    description = description,
    imagePath = imagePath,
    tracks = tracks.map { it.toDto() },
    year = year
)

fun PlaylistDto.toPlaylist() = Playlist(
    id = id,
    name = name,
    description = description,
    imagePath = imagePath,
    tracks = tracks.map { it.toTrack() },
    year = year
)