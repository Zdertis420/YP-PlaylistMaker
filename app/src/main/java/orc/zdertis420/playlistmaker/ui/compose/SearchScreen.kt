package orc.zdertis420.playlistmaker.ui.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.theme.BackgroundColor
import orc.zdertis420.playlistmaker.ui.theme.CursorBlue
import orc.zdertis420.playlistmaker.ui.theme.DarkBackground
import orc.zdertis420.playlistmaker.ui.theme.Gray
import orc.zdertis420.playlistmaker.ui.theme.StandardTextColor
import orc.zdertis420.playlistmaker.ui.theme.White
import orc.zdertis420.playlistmaker.ui.theme.YSDisplay
import orc.zdertis420.playlistmaker.ui.viewmodel.states.SearchState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onClearSearchClicked: () -> Unit,
    onTrackClicked: (Track) -> Unit,
    onRefreshClicked: () -> Unit,
    onClearHistoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) DarkBackground else BackgroundColor)
    ) {
        Text(
            text = stringResource(id = R.string.search),
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) DarkBackground else BackgroundColor)
                .padding(16.dp),
            color = if (isSystemInDarkTheme()) White else StandardTextColor,
            fontSize = 22.sp,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )

        SearchInputField(
            onQueryChanged = { onSearchQueryChanged(it) },
            onClearClicked = { onClearSearchClicked() },
            onTextFieldFocused = {},
            onImeActionSearch = { onSearchClicked(it) }
        )

        Log.d("SEARCH UI STATE", state.toString())
        when (state) {
            is SearchState.Loading -> {
                Column(
                    modifier = modifier
                        .padding(top = 48.dp)
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = CursorBlue)
                }
            }

            is SearchState.Success -> {
                TrackList(
                    tracks = state.tracks,
                    onTrackClicked = onTrackClicked,
                    modifier = modifier
                        .weight(1f)
                        .fillMaxSize()
                )
            }

            is SearchState.Error -> {
                ErrorPlaceholder(
                    onRefreshClicked,
                )
            }

            is SearchState.Empty -> {
                EmptySearchPlaceholder()
            }

            is SearchState.History -> {
                if (state.tracksHistory.isNotEmpty()) {
                    SearchHistoryView(
                        historyTracks = state.tracksHistory,
                        onHistoryTrackClicked = onTrackClicked,
                        onClearHistoryClicked = onClearHistoryClicked,
                        modifier = modifier.weight(1f)

                    )
                }
            }

            is SearchState.None -> {}
        }
    }
}

@Composable
fun SearchHistoryView(
    historyTracks: List<Track>,
    onHistoryTrackClicked: (Track) -> Unit,
    onClearHistoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(top = 24.dp)
            .fillMaxSize(),
    ) {
        item {
            Text(
                text = stringResource(R.string.you_have_searched),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) White else StandardTextColor,
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp
            )
        }

        items(
            items = historyTracks,
            key = { track -> track.trackId }
        ) { track ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TrackListItem(
                    track = track,
                    onTrackClicked = onHistoryTrackClicked
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onClearHistoryClicked,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) White else StandardTextColor,
                        contentColor = if (isSystemInDarkTheme()) StandardTextColor else White
                    )
                ) {
                    Text(
                        stringResource(R.string.clear_history),
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorPlaceholder(
    onRefreshClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) R.drawable.no_connection_dark else R.drawable.no_connection_light
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_connection),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRefreshClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = StandardTextColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                stringResource(R.string.update),
                fontSize = 14.sp,
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptySearchPlaceholder(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.empty_result),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SearchInputField(
    onQueryChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onTextFieldFocused: () -> Unit,
    onImeActionSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            focusManager.clearFocus()
        }
    }

    var text by remember { mutableStateOf("") }

    val textFieldBackgroundColor = Gray

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp)
            .background(
                color = textFieldBackgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        onQueryChanged(text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onTextFieldFocused()
                            }
                        },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onImeActionSearch(text)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }),
                    cursorBrush = SolidColor(CursorBlue),
                    interactionSource = interactionSource
                )

                if (text.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.search),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (text.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        onClearClicked()
                        text = ""
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Preview функции ---

@Preview(showBackground = true, name = "SearchScreen - Loading")
@Composable
fun SearchScreenPreviewLoading() {
    MaterialTheme {
        SearchScreen(
            state = SearchState.Loading,
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchScreen - Content")
@Composable
fun SearchScreenPreviewContent() {
    val sampleTracks = listOf(
        Track(
            trackId = 1,
            trackName = "Track 1",
            artistName = "Artist 1",
            trackTimeMillis = 200000,
            artworkUrl100 = "",
            collectionName = "Album 1",
            releaseDate = "2023",
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = ""
        ),
        Track(
            trackId = 2,
            trackName = "Track 2",
            artistName = "Artist 2",
            trackTimeMillis = 240000,
            artworkUrl100 = "",
            collectionName = "Album 2",
            releaseDate = "2022",
            primaryGenreName = "Pop",
            country = "UK",
            previewUrl = ""
        )
    )
    MaterialTheme {
        SearchScreen(
            state = SearchState.Success(sampleTracks),
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchScreen - Empty Search")
@Composable
fun SearchScreenPreviewEmpty() {
    MaterialTheme {
        SearchScreen(
            state = SearchState.Empty,
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchScreen - Error")
@Composable
fun SearchScreenPreviewError() {
    MaterialTheme {
        SearchScreen(
            state = SearchState.Error,
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchScreen - History Populated")
@Composable
fun SearchScreenPreviewHistoryPopulated() {
    val sampleHistory = listOf(
        Track(
            trackId = 22,
            trackName = "Old Town Road",
            artistName = "Lil Nas X",
            trackTimeMillis = 157000,
            artworkUrl100 = "",
            collectionName = "7",
            releaseDate = "2019",
            primaryGenreName = "Country Rap",
            country = "USA",
            previewUrl = ""
        ),
        Track(
            trackId = 11,
            trackName = "bad guy",
            artistName = "Billie Eilish",
            trackTimeMillis = 194000,
            artworkUrl100 = "",
            collectionName = "When We All Fall Asleep, Where Do We Go?",
            releaseDate = "2019",
            primaryGenreName = "Pop",
            country = "USA",
            previewUrl = ""
        )
    )
    MaterialTheme {
        SearchScreen(
            state = SearchState.History(sampleHistory),
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchScreen - History Empty")
@Composable
fun SearchScreenPreviewHistoryEmpty() {
    MaterialTheme {
        SearchScreen(
            state = SearchState.History(emptyList()),
            onSearchQueryChanged = {},
            onSearchClicked = {},
            onClearSearchClicked = {},
            onTrackClicked = {},
            onClearHistoryClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "TrackList Preview")
@Composable
fun TrackListPreview() {
    val sampleTracks = listOf(
        Track(
            trackId = 1,
            trackName = "Track 1",
            artistName = "Artist 1",
            trackTimeMillis = 200000,
            artworkUrl100 = "",
            collectionName = "Album 1",
            releaseDate = "2023",
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = ""
        ),
        Track(
            trackId = 2,
            trackName = "Track 2",
            artistName = "Artist 2",
            trackTimeMillis = 240000,
            artworkUrl100 = "",
            collectionName = "Album 2",
            releaseDate = "2022",
            primaryGenreName = "Pop",
            country = "UK",
            previewUrl = ""
        ),
        Track(
            trackId = 3,
            trackName = "Track 3",
            artistName = "Artist 3",
            trackTimeMillis = 180000,
            artworkUrl100 = "",
            collectionName = "Album 3",
            releaseDate = "2021",
            primaryGenreName = "Jazz",
            country = "France",
            previewUrl = ""
        )
    )
    MaterialTheme {
        TrackList(tracks = sampleTracks, onTrackClicked = {})
    }
}
