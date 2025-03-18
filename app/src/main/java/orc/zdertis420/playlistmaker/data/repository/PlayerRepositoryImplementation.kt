package orc.zdertis420.playlistmaker.data.repository

import android.media.MediaPlayer
import android.util.Log
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImplementation : PlayerRepository {

    enum class PlayerState {
        IDLE, PREPARING, PREPARED, STARTED, PAUSED, COMPLETED, ERROR, RELEASED
    }

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.IDLE

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompleted: () -> Unit) {
        playerState = PlayerState.PREPARING

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    playerState = PlayerState.PREPARED
                }

                setOnErrorListener { _, error, extras ->
                    playerState = PlayerState.ERROR
                    Log.e("ERROR", "MediaPlayer error: $error\nExtra info: $extras")

                    true
                }

                setOnCompletionListener {
                    playerState = PlayerState.COMPLETED
                }
            } catch (e: Exception) {
                playerState = PlayerState.ERROR
                Log.e("ERROR", "Error while preparing MediaPlayer: $e")
            }
        }
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
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}