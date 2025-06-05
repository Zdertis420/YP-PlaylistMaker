package orc.zdertis420.playlistmaker.domain.repository

import android.content.Context
import android.net.Uri


interface SaveImageRepository {
    suspend fun saveImage(uri: Uri, context: Context): String
}