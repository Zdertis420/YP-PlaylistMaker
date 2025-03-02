package orc.zdertis420.playlistmaker.data.repository

import android.media.MediaPlayer
import android.util.Log
import orc.zdertis420.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImplementation : PlayerRepository {

    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompleted: () -> Unit) {
        try {
            mediaPlayer.setDataSource(url)
        } catch (npe: NullPointerException) {
            Log.e("APPLE", "OFFICE OF FAGGOTS")

            return
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}