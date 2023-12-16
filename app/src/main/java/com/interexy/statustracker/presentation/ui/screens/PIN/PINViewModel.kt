package com.interexy.statustracker.presentation.ui.screens.PIN

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.doman.repository.StatusTrackerRepository
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.NavigationScreen
import com.interexy.statustracker.doman.util.Resource
import com.interexy.statustracker.doman.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PINViewModel @Inject constructor(
    private val repository: StatusTrackerRepository,
    @ApplicationContext application: Context
) : ViewModel() {
    val TAG = "PINViewModel"
    private val _navigateTo = MutableLiveData<String>()
    val navigateTo: LiveData<String> = _navigateTo
    var loadingProgressBar by mutableStateOf(false)
    val dataStore = StatusTrackerDataStore(application)
    val _FCMToken = dataStore.getFCMToken
    var FCMToken by mutableStateOf("")

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()



    init {
        try {
            viewModelScope.launch {
                _FCMToken.collect{
                    if (it!= null){
                        FCMToken = it
                    }
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun startSession(token: StartSessionPayload) {
        loadingProgressBar = true
        viewModelScope.launch {
            when (val result = repository.startSession(token)) {
                is Resource.Success -> {
                    Log.d(TAG, "startSession:${result.data?.message} ")

                    if (result.data?.message == "Session initialized successfully") {
                        addInitialDataToRealtimeDB(token.token)
                    } else {
                        loadingProgressBar = false
                        sentUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "Please enter correct pin"
                            )
                        )
                    }
                }

                is Resource.Error -> {
                }
            }
        }
    }


    fun addInitialDataToRealtimeDB(token: String) {
        val database = Firebase.database
        val myRef = database.getReference("accounts")
        var indexOfChild = 0
        val account = Account(
            unique_code = token,
            status = "Pending",
            device_token = FCMToken
        )

        viewModelScope.launch {
            dataStore.savedUniqueCode(token)
        }

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val accounts = dataSnapshot.children.mapNotNull { it.getValue(Account::class.java) }
                indexOfChild = accounts.size

                // Now you can use the list of accounts and its size
//                println("Number of accounts: $indexOfChild")
//                accounts.forEach { account ->
//                    println("Account: $account")
//                }
                // Add the new account with the new index
                myRef.child(indexOfChild.toString()).setValue(account)
                onNavigateTo(NavigationScreen.LaunchWhatsApp.route)
                loadingProgressBar = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })

    }


    fun onNavigateTo(destination: String?) {
        _navigateTo.value = destination
    }


    private fun sentUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}