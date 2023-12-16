package com.interexy.statustracker.presentation.ui.screens.Splash

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.NavigationScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    var loginSuccessful by mutableStateOf(false)
    val TAG = "SPLASHVIEWMODEL"

    val dataStore = StatusTrackerDataStore(context)
    val _unique_code = dataStore.getUniqueCode
    val _isUserLogin = dataStore.getUserLogin
    val isUserLogin = mutableStateOf(false)


    init {
        viewModelScope.launch {
            try {
                _isUserLogin.collect{
                    if (it!= null){
                        isUserLogin.value = it
                        if (it == true){
                            loginSuccessful = true
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }


}