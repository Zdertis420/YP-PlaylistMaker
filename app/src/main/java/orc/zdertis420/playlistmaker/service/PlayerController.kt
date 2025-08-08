package orc.zdertis420.playlistmaker.service

import kotlinx.coroutines.flow.StateFlow
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState

interface PlayerController {
    fun preparePlayer()

    fun getPlayerState(): StateFlow<PlayerState>

    fun isPlaying(): Boolean?

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun notificationOff()

    fun delete()

    fun updateNotification()
}