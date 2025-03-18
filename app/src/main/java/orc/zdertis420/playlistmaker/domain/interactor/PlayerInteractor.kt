package orc.zdertis420.playlistmaker.domain.interactor

interface PlayerInteractor {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit
    )

    fun start()

    fun pause()

    fun release()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int
}