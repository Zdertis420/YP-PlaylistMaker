package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import orc.zdertis420.playlistmaker.domain.repository.SaveImageRepository
import org.koin.core.logger.Logger
import java.io.File
import java.io.FileOutputStream

class SaveImageRepositoryImplementation(private val context: Context) : SaveImageRepository {
    override suspend fun saveImage(uri: Uri, /*context: Context*/): String {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)

                context.contentResolver.openInputStream(uri).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                            ?: throw IllegalArgumentException("Cannot open input stream from URI")
                    }
                }
                file.absolutePath
            } catch (e: Exception) {
                Log.e("IMAGE REPO", e.toString())
                "пизда ему"
            }
        }
    }
}