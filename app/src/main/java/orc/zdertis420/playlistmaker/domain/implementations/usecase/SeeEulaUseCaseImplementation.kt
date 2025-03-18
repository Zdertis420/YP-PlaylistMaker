package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase

class SeeEulaUseCaseImplementation(private val context: Context) : SeeEulaUseCase {
    override fun seeEula() {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, context.getString(R.string.offer).toUri())
        )
    }
}