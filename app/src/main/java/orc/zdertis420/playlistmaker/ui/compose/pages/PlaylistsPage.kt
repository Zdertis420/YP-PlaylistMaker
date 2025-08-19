package orc.zdertis420.playlistmaker.ui.compose.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.theme.StandardTextColor
import orc.zdertis420.playlistmaker.ui.theme.White
import orc.zdertis420.playlistmaker.ui.theme.YSDisplay
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState

@Composable
fun PlaylistsPage(
    modifier: Modifier = Modifier,
    state: PlaylistsState,
    onPlaylistClicked: (Playlist) -> Unit,
    onNewPlaylistClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNewPlaylistClicked,
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) White else StandardTextColor,
                contentColor = if (isSystemInDarkTheme()) StandardTextColor else White
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
        ) {
            Text(
                stringResource(R.string.new_playlist),
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (state) {
                is PlaylistsState.Empty -> {
                    EmptyStatePlaylists(modifier = Modifier.fillMaxSize())
                }

                is PlaylistsState.Playlists -> {
                    PlaylistsGrid(
                        playlists = state.playlists,
                        onPlaylistClicked = onPlaylistClicked,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is PlaylistsState.Error -> Toast.makeText(
                    LocalContext.current, stringResource(R.string.loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun EmptyStatePlaylists(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) R.drawable.empty_result_dark else R.drawable.empty_result_light
            ),
            contentDescription = stringResource(id = R.string.no_playlists),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.no_playlists),
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSystemInDarkTheme()) White else StandardTextColor,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlaylistsGrid(
    playlists: List<Playlist>,
    onPlaylistClicked: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    if (playlists.isEmpty()) {
        EmptyStatePlaylists(modifier = Modifier.fillMaxSize())
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding) / 2)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = playlists,
            key = { playlist -> playlist.id }
        ) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onPlaylistClicked = { onPlaylistClicked(playlist) }
            )
        }
    }
}


@Composable
fun PlaylistItem(
    playlist: Playlist,
    onPlaylistClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onPlaylistClicked)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(playlist.imagePath)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentDescription = null,
            modifier = Modifier
                .width(160.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop
        )


        Text(
            text = playlist.name,
            modifier = Modifier
                .width(160.dp)
                .height(18.dp),
            fontSize = 12.sp,
            color = if (isSystemInDarkTheme()) White else StandardTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = pluralStringResourceTracks(playlist.tracks.size),
            modifier = Modifier
                .width(160.dp)
                .height(18.dp),
            fontSize = 12.sp,
            color = if (isSystemInDarkTheme()) White else StandardTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun pluralStringResourceTracks(count: Int): String {
    return LocalResources.current.getQuantityString(R.plurals.tracks_plurals, count, count)
}

@Preview(showBackground = true, name = "Playlists Empty")
@Composable
fun PlaylistsPageEmptyPreview() {
    PlaylistsPage(
        state = PlaylistsState.Empty,
        onPlaylistClicked = {},
        onNewPlaylistClicked = {}
    )
}

@Preview(showBackground = true, name = "Playlists Content")
@Composable
fun PlaylistsPageContentPreview() {
    val samplePlaylists = listOf(
        Playlist(
            id = 1L,
            name = "Мой рок",
            imagePath = null,
            tracks = List(12) {
                Track(
                    it.toLong(),
                    "",
                    "",
                    it.toLong(),
                    it.toString(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            },
            year = 2020,
            description = ""
        ),
        Playlist(
            id = 2L,
            name = "Для тренировок",
            imagePath = null,
            tracks = List(12) {
                Track(
                    it.toLong(),
                    "",
                    "",
                    it.toLong(),
                    it.toString(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            },
            year = 2021,
            description = ""
        ),
        Playlist(
            id = 3L,
            name = "В дорогу с очень длинным названием в несколько строк для проверки как это будет выглядеть на экране",
            imagePath = null,
            tracks = List(12) {
                Track(
                    it.toLong(),
                    "",
                    "",
                    it.toLong(),
                    it.toString(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            },
            year = 2022,
            description = ""
        ),
        Playlist(
            id = 4L,
            name = "Любимое",
            imagePath = null,
            tracks = List(12) {
                Track(
                    it.toLong(),
                    "",
                    "",
                    it.toLong(),
                    it.toString(),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            },
            year = 2023,
            description = ""
        )
    )
    PlaylistsPage(
        state = PlaylistsState.Playlists(samplePlaylists),
        onPlaylistClicked = {},
        onNewPlaylistClicked = {}
    )
}

@Preview(showBackground = true, name = "PlaylistItem (Standalone)")
@Composable
fun StandalonePlaylistItemPreview() {
    PlaylistItem(
        playlist = Playlist(
            id = 1L,
            name = "Классика Рока для Долгих Поездок",
            imagePath = null,
            tracks = List(125) {
                Track(
                    trackId = it.toLong(),
                    trackName = "Track $it",
                    artistName = "Artist",
                    artworkUrl100 = "",
                    trackTimeMillis = 0,
                    collectionName = "",
                    releaseDate = "",
                    primaryGenreName = "",
                    country = "",
                    previewUrl = ""
                )
            },
            description = "Описание для превью",
            year = 2023
        ),
        onPlaylistClicked = {}
    )
}