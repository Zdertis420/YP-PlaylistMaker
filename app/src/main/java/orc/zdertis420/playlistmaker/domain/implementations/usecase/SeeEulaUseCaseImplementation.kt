package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Intent
import orc.zdertis420.playlistmaker.domain.repository.SeeEulaRepository
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase

class SeeEulaUseCaseImplementation(val seeEulaRepository: SeeEulaRepository) : SeeEulaUseCase {
    override fun seeEula(): Intent {
        return seeEulaRepository.seeEula()
    }
}