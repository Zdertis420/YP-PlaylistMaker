package orc.zdertis420.playlistmaker.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.ui.theme.BackgroundColor
import orc.zdertis420.playlistmaker.ui.theme.DarkBackground
import orc.zdertis420.playlistmaker.ui.theme.SettingsActivityIconFillColor
import orc.zdertis420.playlistmaker.ui.theme.StandardTextColor
import orc.zdertis420.playlistmaker.ui.theme.SwitchThumbActiveColor
import orc.zdertis420.playlistmaker.ui.theme.SwitchThumbInactiveColor
import orc.zdertis420.playlistmaker.ui.theme.SwitchTrackActiveColor
import orc.zdertis420.playlistmaker.ui.theme.SwitchTrackInactiveColor
import orc.zdertis420.playlistmaker.ui.theme.Transparent
import orc.zdertis420.playlistmaker.ui.theme.White
import orc.zdertis420.playlistmaker.ui.theme.YSDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onEulaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Text(
                text = stringResource(id = R.string.settings),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) DarkBackground else BackgroundColor)
                    .padding(16.dp), // Пример отступов
                color = if (isDarkTheme) White else StandardTextColor,
                fontSize = 22.sp,
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxSize()
                .background(if (isDarkTheme) DarkBackground else BackgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.dark_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDarkTheme) White else StandardTextColor,
                    modifier = Modifier.weight(1f),
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeToggle,
                    colors = SwitchDefaults.colors(
                        uncheckedTrackColor = SwitchTrackInactiveColor,
                        uncheckedThumbColor = SwitchThumbInactiveColor,
                        uncheckedBorderColor = Transparent,
                        checkedTrackColor = SwitchTrackActiveColor,
                        checkedThumbColor = SwitchThumbActiveColor,
                        checkedBorderColor = Transparent
                    )
                )
            }

            SettingsItem(
                text = stringResource(id = R.string.share),
                iconPainter = painterResource(id = R.drawable.share),
                onClick = onShareClick,
                isDarkTheme = isDarkTheme
            )

            SettingsItem(
                text = stringResource(id = R.string.support),
                iconPainter = painterResource(id = R.drawable.support),
                onClick = onSupportClick,
                isDarkTheme = isDarkTheme
            )

            SettingsItem(
                text = stringResource(id = R.string.user_agreement),
                iconPainter = painterResource(id = R.drawable.arrow_right),
                onClick = onEulaClick,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
fun SettingsItem(
    text: String,
    iconPainter: androidx.compose.ui.graphics.painter.Painter, // Changed to Painter for flexibility
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge, // Apply appropriate text style
            color = if (isDarkTheme) White else StandardTextColor,
            modifier = Modifier.weight(1f),
            fontFamily = YSDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Icon(
            painter = iconPainter,
            contentDescription = null, // Decorative icon
            tint = if (isDarkTheme) White else SettingsActivityIconFillColor
        )
    }
}


@Preview(showBackground = true, name = "Settings Screen Light Theme")
@Composable
fun SettingsScreenLightPreview() {
    SettingsScreen(
        isDarkTheme = false,
        onThemeToggle = {},
        onShareClick = {},
        onSupportClick = {},
        onEulaClick = {}
    )
}

@Preview(showBackground = true, name = "Settings Screen Dark Theme")
@Composable
fun SettingsScreenDarkPreview() {
    SettingsScreen(
        isDarkTheme = true,
        onThemeToggle = {},
        onShareClick = {},
        onSupportClick = {},
        onEulaClick = {}
    )
}
