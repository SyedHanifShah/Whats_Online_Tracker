package com.interexy.statustracker.doman.util

import com.google.firebase.database.PropertyName

data class Account(
    var contact_number: String = "",
    var device_token: String = "",
    var end_date_time: String = "",
    var following: String = "",
    @get:PropertyName("is_sleep")
    @set:PropertyName("is_sleep")
    var is_sleep: Int = 1,
    var name: String = "",
    var contact_list: List<MyContacts> = emptyList(),
    var recent_activities: List<RecentActivity> = emptyList(),
    var profile_pic: String = "",
    var settings: Settings? = Settings(),
    var status: String = "",
    var unique_code: String = ""
)

data class MyContacts(
    var contact_active: String = "no",
    var contact_number: String = "",
    var id: Int = 1,
    var name: String = "",
    var online_status: String = "",
    var profile_pic: String = ""
)


data class RecentActivity(
    val account_online: Int = 0,
    val contact_number: String = "",
    val date_time: String = "",
    val name: String = ""
)


