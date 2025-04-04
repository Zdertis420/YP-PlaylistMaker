package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.repository.ShareAppRepository

class ShareAppRepositoryImplementation(private val context: Context) : ShareAppRepository {
    override fun shareApp(): Intent {
        return Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.course)
            )
            type = "text/plain"
        }, null)
    }
}