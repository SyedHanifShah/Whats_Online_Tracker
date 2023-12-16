package com.interexy.statustracker.doman.util

data class Settings(
    var show_notification: Boolean = true,
    var language: String = "English",
    var offline_notification: Boolean = false,
    var online_notification: Boolean = false,
    var ratings: Int? = 0,
    var darkMode: Boolean = false
)