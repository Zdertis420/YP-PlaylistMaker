package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.repository.SeeEulaRepository

class SeeEulaRepositoryImplementation(private val context: Context) : SeeEulaRepository {
    override fun seeEula() {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, context.getString(R.string.offer).toUri())
        )
    }
}