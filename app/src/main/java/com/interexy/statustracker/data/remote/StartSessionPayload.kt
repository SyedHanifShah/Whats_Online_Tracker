package com.interexy.statustracker.data.remote

import com.google.gson.annotations.SerializedName

data class StartSessionPayload(
@SerializedName("token") val token:String
)
