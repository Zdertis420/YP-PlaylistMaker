package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Context
import android.net.Uri
import orc.zdertis420.playlistmaker.domain.repository.SaveImageRepository
import orc.zdertis420.playlistmaker.domain.usecase.SaveImageUseCase

class SaveImageUseCaseImplementation(private val saveImageRepository: SaveImageRepository) : SaveImageUseCase {
    override suspend operator fun invoke(uri: Uri, /*context: Context*/): String {
        return saveImageRepository.saveImage(uri)
    }
}