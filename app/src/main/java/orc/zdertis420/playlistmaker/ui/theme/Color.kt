package orc.zdertis420.playlistmaker.ui.theme

import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Gray = Color(0xFFE6E8EB) // Используется как switch_track_inactive_color, drag
val MainActivityButtonColor = Color(0xFF1A1B22) // Также standard_text_color, unchangeable_standard_text_color
val StandardTextColor = Color(0xFF1A1B22)
val UnchangeableStandardTextColor = Color(0xFF1A1B22)
val Transparent = Color(0x00000000)
val BackgroundMain = Color(0xFF3772E7) // Также unchangeable_background_main, switch_thumb_active_color, focused_box_color, focused_hint_color
val UnchangeableBackgroundMain = Color(0xFF3772E7)
val BackgroundColor = White // В XML: @color/white
val SwitchTrackInactiveColor = Color(0xFFE6E8EB)
val SwitchTrackActiveColor = Color(0xFF9FBBF3)
val SwitchThumbInactiveColor = Color(0xFFAEAFB4) // Также settings_activity_icon_fill_color, subtext, unchangeable_subtext, why, unfocused_box_color
val SwitchThumbActiveColor = Color(0xFF3772E7)
val SettingsActivityIconFillColor = Color(0xFFAEAFB4)
val CursorBlue = Color(0xFF3772E7)
val Subtext = Color(0xFFAEAFB4)
val UnchangeableSubtext = Color(0xFFAEAFB4)
val Why = Color(0xFFAEAFB4) // Такое же значение, как у Subtext
val PlayerPageButton = Color(0xFFC5C6C7)
val LikeRed = Color(0xFFF56B6C)
val UnfocusedBoxColor = Color(0xFFAEAFB4)
val FocusedBoxColor = Color(0xFF3772E7)
// unfocused_hint_color в XML ссылается на standard_text_color
val UnfocusedHintColor = StandardTextColor
val FocusedHintColor = Color(0xFF3772E7)
val Drag = Color(0xFFE6E8EB)

val DarkBackground = StandardTextColor // Типичный темный фон
val DarkSurface = Color(0xFF1E1E1E)   // Для карточек, диалогов и т.д. в темной теме
val DarkPrimary = BackgroundMain      // Можно оставить тот же Primary, или сделать его светлее/темнее
val DarkOnPrimary = White             // Текст/иконки на Primary

val DarkTextPrimary = White           // Основной текст в темной теме
val DarkTextSecondary = Color(0xFFB0B0B0) // Второстепенный текст в темной теме
val DarkIconTint = Color(0xFFB0B0B0)       // Для иконок, которые не являются частью Primary/Secondary интерактивных элементов
