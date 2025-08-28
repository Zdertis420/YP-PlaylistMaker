package orc.zdertis420.playlistmaker.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.compose.pages.LikedTracksPage
import orc.zdertis420.playlistmaker.ui.compose.pages.PlaylistsPage
import orc.zdertis420.playlistmaker.ui.theme.BackgroundColor
import orc.zdertis420.playlistmaker.ui.theme.DarkBackground
import orc.zdertis420.playlistmaker.ui.theme.StandardTextColor
import orc.zdertis420.playlistmaker.ui.theme.Transparent
import orc.zdertis420.playlistmaker.ui.theme.White
import orc.zdertis420.playlistmaker.ui.theme.YSDisplay
import orc.zdertis420.playlistmaker.ui.viewmodel.states.LikedState
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    modifier: Modifier = Modifier,
    likedState: LikedState,
    playlistsState: PlaylistsState,
    onTrackClicked: (Track) -> Unit,
    onPlaylistClicked: (Playlist) -> Unit,
    onNewPlaylistClicked: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val tabTitles = listOf(
        stringResource(id = R.string.liked_tracks),
        stringResource(id = R.string.playlists)
    )

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.media_library),
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) DarkBackground else BackgroundColor)
                .padding(16.dp),
            color = if (isSystemInDarkTheme()) White else StandardTextColor,
            fontSize = 22.sp,
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium
        )

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = if (isSystemInDarkTheme()) StandardTextColor else White,
            contentColor = if (isSystemInDarkTheme()) White else StandardTextColor,
            indicator = {
                val isDark = isSystemInDarkTheme()
                val indicatorColor = if (isDark) White else StandardTextColor
                val tabCount = tabTitles.size

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                ) {
                    val tabWidth = maxWidth
                    val indicatorWidth = tabWidth * 0.8f

                    val animatedIndex = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                        .coerceIn(0f, (tabCount - 1).toFloat())

                    val left = tabWidth * animatedIndex + (tabWidth - indicatorWidth) / 2

                    val density = LocalDensity.current
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = with(density) { left.toPx().roundToInt() },
                                    y = 0
                                )
                            }
                            .width(indicatorWidth)
                            .height(2.dp)
                            .background(indicatorColor)
                    )
                }
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontFamily = YSDisplay,
                            fontWeight = FontWeight.Medium,
                            color = if (isSystemInDarkTheme()) White else StandardTextColor
                        )
                    },
                    selectedContentColor = StandardTextColor,
                    unselectedContentColor = Transparent
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LikedTracksPage(
                    state = likedState,
                    onTrackClicked = onTrackClicked,
                    modifier = Modifier.fillMaxSize()
                )

                1 -> PlaylistsPage(
                    state = playlistsState,
                    onPlaylistClicked = onPlaylistClicked,
                    onNewPlaylistClicked = onNewPlaylistClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MediaLibraryScreenPreviewLiked() {
    MediaLibraryScreen(
        likedState = LikedState.Empty,
        playlistsState = PlaylistsState.Empty,
        onTrackClicked = {},
        onPlaylistClicked = {},
        onNewPlaylistClicked = {},
    )
}

