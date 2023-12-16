package com.interexy.statustracker.presentation.ui.screens.Settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.interexy.statustracker.R
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.presentation.theme.BackgroundDarkColor
import com.interexy.statustracker.presentation.theme.BackgroundDarkThemeLightColor
import com.interexy.statustracker.presentation.theme.MyYellowColor
import com.interexy.statustracker.presentation.theme.SilverGray
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import com.interexy.statustracker.presentation.theme.WhatsAppDarkColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(), isDark: Boolean
) {
    val robotoFamily = MFontFamily()
    val lightMode = viewModel.isDarkMode
    val showNotificationMode = viewModel.isNotificationOn.value
    val isDarkTheme = isSystemInDarkTheme()


    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Settings", style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = robotoFamily.RobotoFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                ), modifier = Modifier.padding(start = 5.dp)
            )
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = if (isDarkTheme || isDark) BackgroundDarkColor else Color.White
        ), navigationIcon = {
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = painterResource(id = R.drawable.icon_settings),
                contentDescription = "Settings"
            )
        })
    }) { padding ->
        padding
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 15.dp, end = 20.dp)
        ) {

            SettingCardTitle("Theme", topSpace = 20.dp)
            var icon = R.drawable.light_icon
            var subTitle = "Disabled"

            if (viewModel.isDarkMode.value) {
                icon = R.drawable.baseline_dark_mode_24
                subTitle = "Enabled"
            } else {
                icon = R.drawable.light_icon
                subTitle = "Disabled"
            }
            SettingCardView(mode = lightMode.value,
                icon = icon,
                iconTint = Color.Black,
                title = "Dark Mode",
                subTitle = subTitle,
                onModeChange = {
                    viewModel.changeSetting(it, "darkMode")
                    viewModel.isDarkMode.value = it
                },
                isDark = isDark
                )
            SettingCardTitle("Notifications", topSpace = 10.dp)

            var notificationIcon = R.drawable.notifications_icon
            var notificationSubTitle = "Enabled"

            if (viewModel.isNotificationOn.value) {
                notificationIcon = R.drawable.notifications_icon
                notificationSubTitle = "Enabled"
            } else {
                notificationIcon = R.drawable.notifications_off
                notificationSubTitle = "Disabled"
            }


            SettingCardView(mode = showNotificationMode,
                icon = notificationIcon,
                iconTint = MyYellowColor,
                title = "Show Notifications",
                subTitle = notificationSubTitle,
                onModeChange = {
                    viewModel.changeSetting(it, "show_notification")
                    viewModel.isNotificationOn.value = it
                },
                isDark = isDark
                )


        }
    }
}


@Composable
fun SettingCardView(
    mode: Boolean,
    icon: Int,
    iconTint: Color,
    title: String,
    subTitle: String,
    isDark: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    val robotoFamily = MFontFamily()
    val isDarkTheme = isSystemInDarkTheme()


    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDark || isDarkTheme) BackgroundDarkThemeLightColor else SilverGray,

        ), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            ) {

                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title, style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = subTitle, style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Switch(checked = mode, onCheckedChange = {
                onModeChange(it)
            }, thumbContent = {
                Icon(
                    imageVector = if (mode) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = if (mode) Color.Green else Color.Red
                )
            })
        }
    }
}


@Composable
fun SettingCardTitle(title: String, topSpace: Dp) {
    val robotoFamily = MFontFamily()

    Spacer(modifier = Modifier.height(topSpace))
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = robotoFamily.RobotoFamily, fontWeight = FontWeight.Light, fontSize = 16.sp
        ),
    )
}


@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(isDark = false)
}