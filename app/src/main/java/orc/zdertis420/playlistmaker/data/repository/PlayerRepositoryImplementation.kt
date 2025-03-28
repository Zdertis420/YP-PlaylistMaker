package orc.zdertis420.playlistmaker.data.repository

import android.media.MediaPlayer
import android.util.Log
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository
import kotlin.toString

class PlayerRepositoryImplementation : PlayerRepository {

    enum class PlayerState {
        IDLE, PREPARING, PREPARED, STARTED, PAUSED, COMPLETED, ERROR, RELEASED
    }

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.IDLE

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompleted: () -> Unit) {
        Log.d("THREAD", "preparePlayer start: ${Thread.currentThread().name}")
        Log.d("PLAYER", "preparePlayer start")

        playerState = PlayerState.PREPARING
        Log.d("STATE", playerState.toString())

        Log.d("PLAYER", "MediaPlayer created")
        mediaPlayer = MediaPlayer()

        Log.d("PLAYER", "MediaPlayer started apply")
        mediaPlayer?.apply {
            try {
                Log.d("PLAYER", "setDataSource")
                setDataSource(url)
                Log.d("PLAYER", "prepareAsync")
                prepareAsync()

                Log.d("PLAYER", "setOnPreparedListener")
                setOnPreparedListener {
                    Log.d("THREAD", "setOnPreparedListener start: ${Thread.currentThread().name}")
                    Log.d("PLAYER", "setOnPreparedListener start")
                    //onPrepared()
                    Log.d("PLAYER", "setOnPreparedListener end")
                    Log.d("THREAD", "setOnPreparedListener end: ${Thread.currentThread().name}")
                }

                Log.d("PLAYER", "setOnErrorListener")
                setOnErrorListener { _, error, extras ->
                    Log.d("THREAD", "setOnErrorListener start: ${Thread.currentThread().name}")
                    Log.d("PLAYER", "setOnErrorListener start")
                    // ...
                    Log.d("PLAYER", "setOnErrorListener end")
                    Log.d("THREAD", "setOnErrorListener end: ${Thread.currentThread().name}")
                    true
                }

                Log.d("PLAYER", "setOnCompletionListener")
                setOnCompletionListener {
                    Log.d("THREAD", "setOnCompletionListener start: ${Thread.currentThread().name}")
                    Log.d("PLAYER", "setOnCompletionListener start")
                    // ...
                    Log.d("PLAYER", "setOnCompletionListener end")
                    Log.d("THREAD", "setOnCompletionListener end: ${Thread.currentThread().name}")
                }
            } catch (e: Exception) {
                playerState = PlayerState.ERROR
                Log.e("ERROR", "Error while preparing MediaPlayer: ${e.message}\n$e")
                e.printStackTrace()
            }
        }

        Log.d("THREAD", "preparePlayer end: ${Thread.currentThread().name}")
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
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}