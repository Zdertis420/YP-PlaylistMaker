package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.repository.ContactSupportRepository

class ContactSupportRepositoryImplementation(private val context: Context) : ContactSupportRepository {
    override fun contactSupport() {
        context.startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SENDTO
            data = "mailto:".toUri()

            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(R.string.email))
            )
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.text)
            )
        }, null))
    }
}