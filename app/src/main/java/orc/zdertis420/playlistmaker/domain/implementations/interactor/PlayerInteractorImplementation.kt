package orc.zdertis420.playlistmaker.domain.implementations.interactor

import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository

class PlayerInteractorImplementation(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {
    override fun prepare(url: String, onPrepared: () -> Unit, onCompleted: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompleted)
    }

    override fun start() {
        playerRepository.startPlayer()
    }

    override fun pause() {
        playerRepository.pausePlayer()
    }

    override fun release() {
        playerRepository.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override fun getCurrentPosition(): Long {
        return playerRepository.getCurrentPosition()
    }
}