package orc.zdertis420.playlistmaker.domain.implementations.usecase

import orc.zdertis420.playlistmaker.domain.repository.SeeEulaRepository
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase

class SeeEulaUseCaseImplementation(val seeEulaRepository: SeeEulaRepository) : SeeEulaUseCase {
    override fun seeEula() {
        seeEulaRepository.seeEula()
    }
}