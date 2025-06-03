package orc.zdertis420.playlistmaker.ui.viewmodel.states

sealed class PlayerState {
    object Preparing : PlayerState()
    object Prepared : PlayerState()
    data class Error(val msg: String) : PlayerState()
    data class Play(val elapsedMillis: Long, val remainingMillis: Long) : PlayerState()
    object Pause : PlayerState()
}