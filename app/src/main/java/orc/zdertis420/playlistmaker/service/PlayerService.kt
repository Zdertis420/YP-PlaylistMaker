package orc.zdertis420.playlistmaker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState

class PlayerService : Service(), PlayerController {

    private val binder = PlayerServiceBinder()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null
    private lateinit var trackUrl: String
    private lateinit var trackName: String
    private lateinit var artistName: String

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 100
        private const val NOTIFICATION_CHANNEL_ID = "music_service_channel"

        const val TRACK_DTO_JSON = "TRACK_DTO_JSON"

        private const val DELAY = 300L
        private const val MAX_DURATION = 30000L
    }

    var isBound = false

    override fun onCreate() {
        super.onCreate()

        Log.e("PlayerService", "OnCreate called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        mediaPlayer = MediaPlayer()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.apply {
            description = "Service for playing music"
//            setSound(null, null)
//            enableVibration(false)
//            enableLights(false)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        sendNotification(createServiceNotification(true))
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService() = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        notificationOff()
        isBound = true

        Log.d("PlayerService", "ДОЛБАЁБ")

        val trackDtoJson = intent?.getStringExtra(TRACK_DTO_JSON)
        if (trackDtoJson != null) {
            val trackDto = Json.decodeFromString<TrackDto>(trackDtoJson)

            trackUrl = trackDto.previewUrl
            trackName = trackDto.trackName
            artistName = trackDto.artistName
        }

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (_playerState.value is PlayerState.Play) {
            sendNotification(createServiceNotification(false))
            isBound = false
        }
        Log.d("PlayerService", "ДОЛБАЁБ")
//        else {
//            delete()
//        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        delete()
    }

    override fun preparePlayer() {
        Log.d("SERVICE", "Track name: $trackName\nArtist name: $artistName\nTrack url: $trackUrl")

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        try {
            mediaPlayer?.setDataSource(trackUrl)
            mediaPlayer?.prepareAsync()
            _playerState.value = PlayerState.Preparing
        } catch (e: Exception) {
            Log.e("PlayerService", "Error in preparePlayer: ${e.message}")
            _playerState.value = PlayerState.Idle
            return
        }

        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.value = PlayerState.Completed
            Log.d("PlayerService", "isBound: $isBound")
        }
        mediaPlayer?.setOnErrorListener { _, what, extra ->
            Log.e("PlayerService", "MediaPlayer Error: What - $what, Extra - $extra")
            _playerState.value = PlayerState.Idle
            notificationOff()
            timerJob?.cancel()
            true
        }
    }

    fun updatePlaybackTime() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                val elapsedTime = it.currentPosition
                val remainingTime = MAX_DURATION - elapsedTime
                _playerState.value = PlayerState.Play(elapsedTime.toLong(), remainingTime)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                updatePlaybackTime()
                delay(DELAY)
            }
        }
    }

    override fun getPlayerState(): StateFlow<PlayerState> {
        return _playerState
    }

    override fun isPlaying(): Boolean? {
        return mediaPlayer?.isPlaying
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.Play(
            mediaPlayer?.currentPosition?.toLong() ?: 0L,
            MAX_DURATION - (mediaPlayer?.currentPosition?.toLong() ?: 0L)
        )
        startTimer()
    }

    override fun pausePlayer() {
        Log.d("SERVICE", " Pause called")

        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Pause
    }

    override fun notificationOff() {
        Log.d("PlayerService", "notificationOff called.")
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun delete() {
        releasePlayer()
        stopSelf()
    }

    override fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        _playerState.value = PlayerState.Idle
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        notificationOff()
        Log.e(
            "PlayerService",
            "releasePlayer() called. Current _playerState: ${_playerState.value}"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra(TRACK_DTO_JSON) == true) {
            val trackDtoJson = intent.getStringExtra(TRACK_DTO_JSON)
            if (trackDtoJson != null) {
                val trackDto = Json.decodeFromString<TrackDto>(trackDtoJson)
                if (!::trackUrl.isInitialized || trackUrl != trackDto.previewUrl) {
                    trackUrl = trackDto.previewUrl
                    trackName = trackDto.trackName
                    artistName = trackDto.artistName
                } else {
                    Log.d(
                        "PlayerService",
                        "onStartCommand: Same track data received, trackName: $trackName"
                    )
                }
            }
        }

        if (!::trackUrl.isInitialized) {
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun createServiceNotification(isSilent: Boolean): Notification {
        Log.d(
            "PlayerService",
            "createServiceNotification called. Track: ${if (::artistName.isInitialized) artistName else "Unknown Artist"} - ${if (::trackName.isInitialized) trackName else "Unknown Track"}"
        )
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(if (::artistName.isInitialized && ::trackName.isInitialized) "$artistName - $trackName" else "Playing music")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(false)
            .setSilent(isSilent)
            .build()
    }

    private fun sendNotification(notification: Notification) {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            notification,
            getForegroundServiceTypeConstant()
        )
    }

    private fun getForegroundServiceTypeConstant(): Int =
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
}
