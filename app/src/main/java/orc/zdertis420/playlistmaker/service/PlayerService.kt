package orc.zdertis420.playlistmaker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
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

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Preparing)

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

    private var isBound = false

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        notificationOff() // Hide notification when bound

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        // If player is playing or prepared (paused), update notification status
        if (mediaPlayer?.isPlaying == true || _playerState.value is PlayerState.Pause || _playerState.value is PlayerState.Prepared) {
            updateNotification()
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun preparePlayer() {
        Log.d("SERVICE", "Track name: $trackName\nArtist name: $artistName\nTrack url: $trackUrl")

        try {
            mediaPlayer?.setDataSource(trackUrl)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            _playerState.value = PlayerState.Preparing
            return
        }

        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.value = PlayerState.Prepared
            // If not bound, keep showing notification until explicitly stopped or new track
            if (!isBound) {
                 updateNotification() // Or decide if a "completed" notification is needed
            } else {
                notificationOff()
            }
        }
        mediaPlayer?.setOnErrorListener { _, what, extra ->
            _playerState.value = PlayerState.Idle
            notificationOff()
            timerJob?.cancel()
            true
        }
    }

    fun updatePlaybackTime() {
        val elapsedTime = mediaPlayer!!.getCurrentPosition()
        val remainingTime = MAX_DURATION - elapsedTime
        _playerState.value = PlayerState.Play(elapsedTime.toLong(), remainingTime)
    }

    override fun updateNotification() {
        Log.d("PlayerService", "updateNotification called. isBound: $isBound, isPlaying: ${mediaPlayer?.isPlaying}")
        if (isBound) {
            Log.d("PlayerService", "updateNotification: Service is BOUND, calling notificationOff()")
            notificationOff()
        } else {
            if (mediaPlayer?.isPlaying == true) {
                Log.d("PlayerService", "updateNotification: Service NOT bound, player IS playing. Starting foreground.")
                val notification = createServiceNotification() // Create notification first
                ServiceCompat.startForeground(
                    this,
                    SERVICE_NOTIFICATION_ID,
                    notification, // Pass the created notification
                    getForegroundServiceTypeConstant()
                )
            } else {
                Log.d("PlayerService", "updateNotification: Service NOT bound, player NOT playing. Calling notificationOff()")
                notificationOff()
            }
        }
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                updatePlaybackTime()
                updateNotification() // This will manage showing/hiding notification based on isBound
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
        startTimer() // This will call updateNotification
    }

    override fun pausePlayer() {
        Log.d("SERVICE", " Pause called")

        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Pause
        updateNotification() // Update notification status (will hide if bound, or keep hidden if unbound)
    }

    override fun notificationOff() {
        Log.d("PlayerService", "notificationOff called.")
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    // Call this method when you want to completely stop and release service resources
    override fun delete() {
        releasePlayer() // releasePlayer already calls notificationOff
        stopSelf()
    }

    override fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        _playerState.value = PlayerState.Idle
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        notificationOff() // Ensure notification is off when player is released
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PlayerService", "onStartCommand received. Intent action: ${intent?.action}")
        if (intent?.hasExtra(TRACK_DTO_JSON) == true) {
            val trackDtoJson = intent.getStringExtra(TRACK_DTO_JSON)
            val trackDto = Json.decodeFromString<TrackDto>(trackDtoJson!!)
            trackUrl = trackDto.previewUrl
            trackName = trackDto.trackName
            artistName = trackDto.artistName
            Log.d("PlayerService", "onStartCommand: Track data re-initialized: $trackName")
        } else {
            Log.d("PlayerService", "onStartCommand: Intent did NOT contain TRACK_DTO_JSON. trackName: $trackName")
        }

        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(), // Pass the created notification
            getForegroundServiceTypeConstant()
        )

        updateNotification()
        return START_STICKY
    }

    private fun createServiceNotification(): Notification {
        Log.d("PlayerService", "createServiceNotification called. Track: $artistName - $trackName")
        // Ensure your icon is valid, simple, and monochrome (typically white).
        // If R.drawable.ic_launcher_foreground is problematic, test with a system icon:
        // .setSmallIcon(android.R.drawable.ic_media_play)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name)) // Use string resource
            .setContentText(if (::artistName.isInitialized && ::trackName.isInitialized) "$artistName - $trackName" else "Playing music")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ASSUMING YOU HAVE THIS ICON
            .setPriority(NotificationCompat.PRIORITY_LOW) // Or PRIORITY_DEFAULT
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
}