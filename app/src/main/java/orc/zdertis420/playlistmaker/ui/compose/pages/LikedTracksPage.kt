package orc.zdertis420.playlistmaker.ui.compose.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.compose.TrackList
import orc.zdertis420.playlistmaker.ui.theme.BackgroundColor
import orc.zdertis420.playlistmaker.ui.theme.CursorBlue
import orc.zdertis420.playlistmaker.ui.theme.DarkBackground
import orc.zdertis420.playlistmaker.ui.viewmodel.states.LikedState

@Composable
fun LikedTracksPage(
    modifier: Modifier = Modifier,
    state: LikedState,
    onTrackClicked: (Track) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) DarkBackground else BackgroundColor)
    ) {
        when (state) {
            is LikedState.Loading -> {
                Spacer(modifier.padding(top = 24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is LikedState.Empty -> ShowEmpty()
            is LikedState.Error -> Toast.makeText(
                LocalContext.current,
                state.msg,
                Toast.LENGTH_SHORT
            ).show()

            is LikedState.LikedTracks -> TrackList(
                tracks = state.tracks,
                onTrackClicked = onTrackClicked
            )
        }
    }
}

@Composable
fun ShowEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) R.drawable.empty_result_dark else R.drawable.empty_result_light
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_media_library),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun LikedTracksPageLoadingPreview() {
    LikedTracksPage(
        state = LikedState.Loading,
        onTrackClicked = {}
    )
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun LikedTracksPageEmptyPreview() {
    LikedTracksPage(
        state = LikedState.Empty,
        onTrackClicked = {}
    )
}

@Preview(showBackground = true, name = "Content State (Few Tracks)")
@Composable
fun LikedTracksPageContentPreview() {
    val sampleTracks = listOf(
        Track(
            trackId = 1,
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            artworkUrl100 = "",
            trackTimeMillis = 301000,
            collectionName = "Nevermind",
            releaseDate = "1991",
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = ""
        ),
        Track(
            trackId = 2,
            trackName = "Bohemian Rhapsody",
            artistName = "Queen",
            artworkUrl100 = "",
            trackTimeMillis = 355000,
            collectionName = "A Night at the Opera",
            releaseDate = "1975",
            primaryGenreName = "Rock",
            country = "UK",
            previewUrl = ""
        ),
        Track(
            trackId = 3,
            trackName = "Stairway to Heaven",
            artistName = "Led Zeppelin",
            artworkUrl100 = "",
            trackTimeMillis = 482000,
            collectionName = "Led Zeppelin IV",
            releaseDate = "1971",
            primaryGenreName = "Rock",
            country = "UK",
            previewUrl = ""
        )
    )
    LikedTracksPage(
        state = LikedState.LikedTracks(sampleTracks),
        onTrackClicked = {}
    )
}

@Preview(showBackground = true, name = "Content State (Many Tracks - for scrolling)")
@Composable
fun LikedTracksPageContentScrollPreview() {
    val sampleTracks = List(20) { index ->
        Track(
            trackId = (index + 1).toLong(),
            trackName = "Track Name ${index + 1}",
            artistName = "Artist ${index + 1}",
            artworkUrl100 = "",
            trackTimeMillis = (180 + index * 5) * 1000L,
            collectionName = "Album ${index % 3 + 1}",
            releaseDate = "202${index % 5}",
            primaryGenreName = if (index % 2 == 0) "Rock" else "Pop",
            country = "Various",
            previewUrl = ""
        )
    }
    LikedTracksPage(
        state = LikedState.LikedTracks(sampleTracks),
        onTrackClicked = {}
    )
}
