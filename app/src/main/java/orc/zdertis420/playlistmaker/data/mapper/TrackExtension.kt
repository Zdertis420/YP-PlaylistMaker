package orc.zdertis420.playlistmaker.data.mapper

import orc.zdertis420.playlistmaker.data.db.entity.TrackDBEntity
import orc.zdertis420.playlistmaker.data.db.entity.CachedTrackDBEntity
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.domain.entities.Track

fun Track.toDto() = TrackDto(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isLiked = isLiked
)


fun TrackDto.toTrack() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isLiked = isLiked
)

fun TrackDto.toCachedTrackDBEntity() = CachedTrackDBEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isLiked = isLiked
)

fun CachedTrackDBEntity.toDto() = TrackDto(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isLiked = isLiked
)

fun TrackDBEntity.toTrack() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isLiked = isLiked
)