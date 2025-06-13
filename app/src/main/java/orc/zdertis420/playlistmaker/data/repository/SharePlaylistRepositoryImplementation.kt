package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.repository.SharePlaylistRepository
import java.text.SimpleDateFormat
import java.util.Locale

class SharePlaylistRepositoryImplementation(private val context: Context) :
    SharePlaylistRepository {
    override fun share(playlist: Playlist): Intent {
        val stringBuilder = StringBuilder()
        val simpleDate by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

        stringBuilder.appendLine(playlist.name)

        if (!playlist.description.isNullOrBlank()) {
            stringBuilder.appendLine(playlist.description)
        }

        stringBuilder.appendLine(
            context.resources.getQuantityString(
                R.plurals.tracks_plurals,
                playlist.tracks.size,
                playlist.tracks.size
            )
        )

        playlist.tracks.forEachIndexed { index, track ->
            stringBuilder.appendLine(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${
                    simpleDate.format(
                        track.trackTimeMillis
                    )
                })"
            )
        }

        return Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                stringBuilder.toString().trimEnd()
            )
            type = "text/plain"
        }, null)
    }
}