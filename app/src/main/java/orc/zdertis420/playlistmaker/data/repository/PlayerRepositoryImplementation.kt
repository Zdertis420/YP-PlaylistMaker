package orc.zdertis420.playlistmaker.data.repository

import android.media.MediaPlayer
import android.util.Log
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImplementation(private var mediaPlayer: MediaPlayer?) : PlayerRepository {

    enum class PlayerState {
        IDLE, PREPARING, PREPARED, STARTED, PAUSED, COMPLETED, ERROR, RELEASED
    }

    private var playerState = PlayerState.IDLE

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompleted: () -> Unit) {

        playerState = PlayerState.PREPARING
        Log.d("STATE", playerState.toString())

        Log.d("PLAYER", "MediaPlayer created")

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        mediaPlayer?.apply {
            try {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    playerState = PlayerState.PREPARED
                    onPrepared.invoke()
                }

                setOnErrorListener { _, error, extras ->
                    playerState = PlayerState.ERROR
                    Log.e("ERROR", "MediaPlayer error: $error\nExtra info: $extras")

                    true
                }

                setOnCompletionListener {
                    playerState = PlayerState.COMPLETED
                    onCompleted.invoke()
                }
            } catch (e: Exception) {
                playerState = PlayerState.ERROR
                Log.e("ERROR", "Error while preparing MediaPlayer: ${e.message}\n$e")
            }
        }

        Log.d("PLAYER", playerState.toString())

    }

    override fun startPlayer() {
        if (playerState == PlayerState.PREPARED || playerState == PlayerState.PAUSED) {
            mediaPlayer?.start()
            playerState = PlayerState.STARTED
        }
    }

    override fun pausePlayer() {
        if (playerState == PlayerState.STARTED) {
            mediaPlayer?.pause()
            playerState = PlayerState.PAUSED
        }
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = PlayerState.RELEASED
        Log.d("STATE", playerState.toString())
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0
    }
}