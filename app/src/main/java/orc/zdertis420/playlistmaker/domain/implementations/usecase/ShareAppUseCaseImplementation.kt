package orc.zdertis420.playlistmaker.domain.implementations.usecase

import orc.zdertis420.playlistmaker.domain.repository.ShareAppRepository
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase

class ShareAppUseCaseImplementation(val shareAppRepository: ShareAppRepository) : ShareAppUseCase {
    override fun shareApp() {
        shareAppRepository.shareApp()
    }
}