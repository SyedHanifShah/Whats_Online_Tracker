package com.interexy.statustracker.doman.repository

import com.interexy.statustracker.data.remote.SessionInitializedDto
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.data.remote.SyncContactsDto
import com.interexy.statustracker.doman.util.Resource
import retrofit2.Response

interface StatusTrackerRepository {
suspend fun startSession(token:StartSessionPayload):Resource<SessionInitializedDto>
suspend fun syncContacts(token:StartSessionPayload):Resource<SyncContactsDto>
}