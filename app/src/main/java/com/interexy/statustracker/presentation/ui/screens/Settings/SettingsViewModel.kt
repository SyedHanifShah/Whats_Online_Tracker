package com.interexy.statustracker.presentation.ui.screens.Settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context
) :ViewModel() {
    val TAG = "SETTINGSVIEWMODEL"
    val dataStore = StatusTrackerDataStore(context)
    val _unique_code = dataStore.getUniqueCode
    val unique_code = mutableStateOf("")
    val userData = mutableStateOf<Account?>(null)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var isNotificationOn = mutableStateOf(true)
    var isDarkMode = mutableStateOf(false)


    init {

        viewModelScope.launch {
            try {
                _unique_code.collect {
                    if (it != null) {
                        unique_code.value = it
                        val query: Query = FirebaseDatabase.getInstance().getReference("accounts")
                            .orderByChild("unique_code").equalTo(it)

                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot in dataSnapshot.children) {
                                    val account = snapshot.getValue(Account::class.java)
                                    if (account != null) {
                                        userData.value = account
                                        val settings = account.settings
                                        if (settings != null) {
                                            isDarkMode.value = settings.darkMode
                                            isNotificationOn.value = settings.show_notification
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(
                                    TAG, "Error querying for account", databaseError.toException()
                                )
                            }
                        })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }





    fun changeSetting(value: Boolean, settingType:String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("accounts")

        val query = databaseReference.orderByChild("unique_code").equalTo(unique_code.value)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there's only one matching record, as unique_code should be unique
                    val accountSnapshot = dataSnapshot.children.first()

                    // Assuming settings is a child of the account object
                    val settingsSnapshot = accountSnapshot.child("settings")
                    if (settingsSnapshot.exists()) {
                        // Assuming show_notifications is a child of the settings object

                        settingsSnapshot.ref.child(settingType).setValue(value)

                    } else {
                        Log.d(TAG, "No settings found for account: ${unique_code}")
                    }
                } else {
                    Log.d(TAG, "No matching record found for unique_code: ${unique_code}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error querying for account", databaseError.toException())
            }
        })
    }












}