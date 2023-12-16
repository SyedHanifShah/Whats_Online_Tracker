package com.interexy.statustracker.doman.util

import androidx.compose.ui.res.painterResource
import com.interexy.statustracker.R

sealed class NavigationScreen(
    val baseRoute: String,
    val route: String,
    val title: String,
    val icon : Int
) {
    object PIN : NavigationScreen(
        baseRoute = "pin",
        route = "pin",
        title = "PIN Screen",
        icon = R.drawable.icon_home
    )
    object Splash : NavigationScreen(
        baseRoute = "Splash",
        route = "Splash",
        title = "Splash",
        icon = R.drawable.icon_home
    )

    object LaunchWhatsApp : NavigationScreen(
        baseRoute = "LaunchWhatsApp",
        route = "LaunchWhatsApp",
        title = "LaunchWhatsApp Screen",
        icon = R.drawable.icon_home
    )

    object ResultScreen : NavigationScreen(
        baseRoute = "ResultScreen",
        route = "ResultScreen",
        title = "Result Screen",
        icon = R.drawable.icon_home

    )
    object HomeScreen : NavigationScreen(
        baseRoute = "homeScreen",
        route = "homeScreen",
        title = "Home",
        icon = R.drawable.icon_home
    )
    object SettingsScreen : NavigationScreen(
        baseRoute = "SettingsScreen",
        route = "SettingsScreen",
        title = "Settings",
        icon = R.drawable.icon_settings
    )
    object ContactsScreen : NavigationScreen(
        baseRoute = "ContactsScreen",
        route = "ContactsScreen",
        title = "Contacts",
        icon = R.drawable.user
    )
    object FeedbackScreen : NavigationScreen(
        baseRoute = "FeedbackScreen",
        route = "feedbackScreen",
        title = "Support",
        icon = R.drawable.support_icon
    )

}