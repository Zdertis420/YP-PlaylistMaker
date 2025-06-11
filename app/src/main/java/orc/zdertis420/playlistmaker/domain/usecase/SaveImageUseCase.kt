package orc.zdertis420.playlistmaker.domain.usecase

import android.content.Context
import android.net.Uri

interface SaveImageUseCase {
    suspend operator fun invoke(uri: Uri, /*context: Context*/): String
}