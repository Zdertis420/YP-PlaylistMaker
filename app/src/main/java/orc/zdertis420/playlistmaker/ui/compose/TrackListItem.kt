package orc.zdertis420.playlistmaker.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.theme.BackgroundColor
import orc.zdertis420.playlistmaker.ui.theme.DarkBackground
import orc.zdertis420.playlistmaker.ui.theme.StandardTextColor
import orc.zdertis420.playlistmaker.ui.theme.Subtext
import orc.zdertis420.playlistmaker.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TrackListItem(
    track: Track,
    onTrackClicked: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isSystemInDarkTheme()) DarkBackground else BackgroundColor)
            .clickable { onTrackClicked(track) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100, // URL обложки
            contentDescription = track.trackName, // Описание для доступности
            placeholder = painterResource(id = R.drawable.placeholder), // Плейсхолдер во время загрузки
            error = painterResource(id = R.drawable.placeholder), // Плейсхолдер при ошибке (можно другой)
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)), // Скругление углов
            contentScale = ContentScale.Crop // Масштабирование
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                color = if (isSystemInDarkTheme()) White else StandardTextColor,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSystemInDarkTheme()) White else Subtext,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = " • ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSystemInDarkTheme()) White else Subtext,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(id = R.string.description),
            tint = if (isSystemInDarkTheme()) White else Subtext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackListItemPreview() {
    val sampleTrack = Track(
        trackId = 1,
        trackName = "Lorem Ipsum Dolor Sit Amet Convey",
        artistName = "Very Long Artist Name to Check Ellipsis",
        trackTimeMillis = 200000,
        artworkUrl100 = "", // Это поле теперь не используется напрямую TrackListItem, но оставляем для полноты модели
        collectionName = "Cool Album",
        releaseDate = "2023",
        primaryGenreName = "Rock",
        country = "USA",
        previewUrl = "lakdsjhg;olasdhglkadjhgkljhg"
    )
    MaterialTheme {
        TrackListItem(track = sampleTrack, onTrackClicked = {})
    }
}
