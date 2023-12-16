package com.interexy.statustracker.data.remote

import com.interexy.statustracker.doman.util.Constants.Companion.SessionInitializedURL
import com.interexy.statustracker.doman.util.Constants.Companion.syncContactsURL
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StatusTrackerApi {

    @POST(SessionInitializedURL)
    suspend fun startSession(
        @Body token: StartSessionPayload
    ):SessionInitializedDto


    @POST(syncContactsURL)
    suspend fun syncContacts(
        @Body token: StartSessionPayload
    ):SyncContactsDto


}