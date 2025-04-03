package orc.zdertis420.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Long
}