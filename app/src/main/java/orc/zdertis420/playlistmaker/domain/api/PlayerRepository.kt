package orc.zdertis420.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit
    )

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int
}