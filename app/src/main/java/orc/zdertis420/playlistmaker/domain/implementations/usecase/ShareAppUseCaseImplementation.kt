package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Context
import android.content.Intent
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase

class ShareAppUseCaseImplementation(private val context: Context) : ShareAppUseCase {
    override fun shareApp() {
        context.startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.course)
            )
            type = "text/plain"
        }, null))
    }
}