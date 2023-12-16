package com.interexy.statustracker.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatusTrackerDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("CornPosDataStore")

        val UNIQUECODE = stringPreferencesKey("unique_Code")
        val CURRENNNTFOLLOWING = stringPreferencesKey("current_following")
        val FCMTOKEN = stringPreferencesKey("fcm-token")
        val ISLOGIN = booleanPreferencesKey("is_login")


    }

    val getUniqueCode: Flow<String?> = context.dataStore.data
        .map{ preferences ->
            preferences[UNIQUECODE] ?: ""
        }


    suspend fun savedUniqueCode(unique_Code: String) {
        context.dataStore.edit { preferences ->
            preferences[UNIQUECODE] = unique_Code
        }
    }

    val getCurrentFollowing: Flow<String?> = context.dataStore.data
        .map{ preferences ->
            preferences[CURRENNNTFOLLOWING] ?: ""
        }


    suspend fun saveCurrentFollowing(value: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENNNTFOLLOWING] = value
        }
    }

    val getFCMToken: Flow<String?> = context.dataStore.data
        .map{ preferences ->
            preferences[FCMTOKEN] ?: ""
        }


    suspend fun saveFCMToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences[FCMTOKEN] = value
        }
    }




    val getUserLogin: Flow<Boolean?> = context.dataStore.data
        .map{ preferences ->
            preferences[ISLOGIN] ?: false
        }


    suspend fun saveUserLogin(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ISLOGIN] = value
        }
    }



}

