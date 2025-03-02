package orc.zdertis420.playlistmaker.domain.implementations

import orc.zdertis420.playlistmaker.domain.api.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.api.PlayerRepository

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

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

}