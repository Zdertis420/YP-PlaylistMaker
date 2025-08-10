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
        const val EXTRA_STARTED_BY_UI_FOR_BINDING = "EXTRA_STARTED_BY_UI_FOR_BINDING"

        private const val DELAY = 300L
        private const val MAX_DURATION = 30000L
    }

    private var isBound = false

    override fun onCreate() {
        super.onCreate()

        Log.e("SERVICE", "OnCreate called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        mediaPlayer = MediaPlayer()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService() = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        val trackDtoJson = intent?.getStringExtra(TRACK_DTO_JSON)
        if (trackDtoJson != null) {
            val trackDto = Json.decodeFromString<TrackDto>(trackDtoJson)

            trackUrl = trackDto.previewUrl
            trackName = trackDto.trackName
            artistName = trackDto.artistName
        }

        isBound = true
        notificationOff()
        Log.d(
            "PlayerService",
            "onBind called. MediaPlayer is null: ${mediaPlayer == null}, isPlaying: ${mediaPlayer?.isPlaying}, playerState: ${_playerState.value}"
        )
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        if (mediaPlayer?.isPlaying == true || _playerState.value is PlayerState.Pause || _playerState.value is PlayerState.Prepared) {
            updateNotification()
        }
        return super.onUnbind(intent)
    }

    override fun preparePlayer() {
        Log.d("SERVICE", "Track name: $trackName\nArtist name: $artistName\nTrack url: $trackUrl")

        if (mediaPlayer == null) { // Если MediaPlayer был освобожден (null)
            Log.d("SERVICE", "preparePlayer: MediaPlayer was null, creating a new instance.")
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
            _playerState.value = PlayerState.Prepared
            if (!isBound) {
                updateNotification()
            } else {
                notificationOff()
            }
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

    override fun updateNotification() {
//        Log.d(
//            "PlayerService",
//            "updateNotification called. isBound: $isBound, playerState: ${_playerState.value}, isPlaying: ${mediaPlayer?.isPlaying}"
//        )

        Log.e("PlayerService", "Service is bound: $isBound")

        if (isBound) {
            Log.d(
                "PlayerService",
                "updateNotification: Service is BOUND, calling notificationOff()"
            )
            notificationOff()
        } else {
            if (mediaPlayer?.isPlaying == true || _playerState.value is PlayerState.Pause || _playerState.value is PlayerState.Prepared) {
                Log.d(
                    "PlayerService",
                    "updateNotification: Service NOT bound, player ACTIVE (playing/paused/prepared). Starting/Keeping foreground."
                )
                val notification = createServiceNotification()
                ServiceCompat.startForeground(
                    this,
                    SERVICE_NOTIFICATION_ID,
                    notification,
                    getForegroundServiceTypeConstant()
                )
            } else {
                Log.d(
                    "PlayerService",
                    "updateNotification: Service NOT bound, player NOT active. Calling notificationOff()"
                )
                notificationOff()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                updatePlaybackTime()
//                updateNotification()
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
        updateNotification()
    }

    override fun pausePlayer() {
        Log.d("SERVICE", " Pause called")

        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Pause
        updateNotification()
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
        Log.d("PlayerService", "onStartCommand received. Intent action: ${intent?.action}")
        val startedByUiForBinding =
            intent?.getBooleanExtra(EXTRA_STARTED_BY_UI_FOR_BINDING, false) == true

        if (intent?.hasExtra(TRACK_DTO_JSON) == true) {
            val trackDtoJson = intent.getStringExtra(TRACK_DTO_JSON)
            if (trackDtoJson != null) {
                val trackDto = Json.decodeFromString<TrackDto>(trackDtoJson)
                if (!::trackUrl.isInitialized || trackUrl != trackDto.previewUrl) {
                    trackUrl = trackDto.previewUrl
                    trackName = trackDto.trackName
                    artistName = trackDto.artistName
                    Log.d("PlayerService", "onStartCommand: New track data initialized: $trackName")
                } else {
                    Log.d(
                        "PlayerService",
                        "onStartCommand: Same track data received, trackName: $trackName"
                    )
                }
            }
        } else {
            Log.d(
                "PlayerService",
                "onStartCommand: Intent did NOT contain TRACK_DTO_JSON. Using existing track data if available. trackName: ${if (::trackName.isInitialized) trackName else "not initialized"}"
            )
        }

        if (!::trackName.isInitialized && !::artistName.isInitialized) {
            Log.w(
                "PlayerService",
                "Track details not available for notification in onStartCommand (or for foreground check)"
            )
        }

        if (::trackUrl.isInitialized) {
            if (!isBound && !startedByUiForBinding) {
                ServiceCompat.startForeground(
                    this,
                    SERVICE_NOTIFICATION_ID,
                    createServiceNotification(),
                    getForegroundServiceTypeConstant()
                )
                Log.d(
                    "PlayerService",
                    "onStartCommand: Service started in foreground (not bound AND not started by UI for binding)."
                )
            } else if (startedByUiForBinding) {
                Log.d(
                    "PlayerService",
                    "onStartCommand: Started by UI for binding. Foreground status will be managed by binding state and player actions."
                )
            } else if (isBound) {
                Log.d(
                    "PlayerService",
                    "onStartCommand: Service is already bound. Foreground status is managed by binding state."
                )
            }
        } else {
            Log.w(
                "PlayerService",
                "onStartCommand: No track URL available, cannot start foreground or prepare player. Stopping service."
            )
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun createServiceNotification(): Notification {
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
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int =
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
}
