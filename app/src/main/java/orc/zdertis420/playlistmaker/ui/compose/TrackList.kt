package orc.zdertis420.playlistmaker.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import orc.zdertis420.playlistmaker.domain.entities.Track

@Composable
fun TrackList(
    tracks: List<Track>,
    onTrackClicked: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(top = 8.dp)) {
        items(
            items = tracks,
            key = { track -> track.trackId }
        ) { track ->
            TrackListItem(
                track = track,
                onTrackClicked = onTrackClicked
            )
        }
    }
}