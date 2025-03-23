package orc.zdertis420.playlistmaker.ui.viewmodel.states

sealed class PlayerState {
    object Prepared : PlayerState()
    data class Error(val msg: String) : PlayerState()
    object Play : PlayerState()
    object Pause : PlayerState()
}