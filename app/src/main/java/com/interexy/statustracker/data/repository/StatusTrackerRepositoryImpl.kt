package com.interexy.statustracker.data.repository

import com.interexy.statustracker.data.remote.SessionInitializedDto
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.data.remote.StatusTrackerApi
import com.interexy.statustracker.data.remote.SyncContactsDto
import com.interexy.statustracker.doman.repository.StatusTrackerRepository
import com.interexy.statustracker.doman.util.Resource
import javax.inject.Inject

class StatusTrackerRepositoryImpl @Inject constructor(
private val api : StatusTrackerApi
) :StatusTrackerRepository{
    override suspend fun startSession(token:StartSessionPayload): Resource<SessionInitializedDto> {
        return try {
         Resource.Success(
             data = api.startSession(token)
         )
     } catch(e: Exception) {
         e.printStackTrace()
         Resource.Error(e.message ?: "An unknown error occurred.")
     }
    }

    override suspend fun syncContacts(token: StartSessionPayload): Resource<SyncContactsDto> {
        return try {
            Resource.Success(
                data = api.syncContacts(token)
            )
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }

    }

}