package com.interexy.statustracker.presentation.ui.screens.Home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
import com.interexy.statustracker.doman.util.MyContacts
import com.interexy.statustracker.doman.util.NavigationScreen
import com.interexy.statustracker.doman.util.RecentActivity
import com.interexy.statustracker.doman.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val TAG = "HOMEVIEWMODEL"
    val dataStore = StatusTrackerDataStore(context)
    val _unique_code = dataStore.getUniqueCode
    val unique_code = mutableStateOf("")
    val _currentFollowing = dataStore.getCurrentFollowing
    val currentFollowing = mutableStateOf("")
    val is_sleep = mutableStateOf(0)
    val userData = mutableStateOf<Account?>(null)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var selectedContactList = mutableStateOf<List<MyContacts>>(emptyList())
    var recentActivitiesList = mutableStateOf<List<RecentActivity>>(emptyList())
    var isUserDisconnected = mutableStateOf(false)


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
                                        is_sleep.value = account.is_sleep
                                        Log.d(TAG, "changeSleepMode: ${account}")
                                        selectedContactList.value = account.contact_list.filter {
                                            it.contact_active == "yes"
                                        }
                                        recentActivitiesList.value = account.recent_activities
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


        viewModelScope.launch {
            try {
                _currentFollowing.collect { mCurrentFollowing ->
                    if (mCurrentFollowing != null) {
                        currentFollowing.value = mCurrentFollowing
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    fun onRemoveAccount() {
        viewModelScope.launch {
            dataStore.savedUniqueCode("")
            dataStore.saveUserLogin(false)
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        val query = databaseReference.orderByChild("unique_code").equalTo(unique_code.value)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there's only one matching record, as unique_code should be unique
                    val accountSnapshot = dataSnapshot.children.first()
                    accountSnapshot.ref.child("status").setValue("Removed")
                    sentUiEvent(UiEvent.Navigate(NavigationScreen.PIN.route))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error querying for account", databaseError.toException())
            }

        })


    }

    fun synchronized() {
        viewModelScope.launch {
            dataStore.savedUniqueCode("")
            dataStore.saveUserLogin(false)
            sentUiEvent(UiEvent.Navigate(NavigationScreen.PIN.route))
        }
    }


    fun onNavigateToContactsScreen() {
        sentUiEvent(UiEvent.Navigate(NavigationScreen.ContactsScreen.route))
        Log.d(TAG, "onNavigateToContactsScreen: ")
    }

    private fun sentUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    fun changeSleepMode() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("accounts")

        // Assuming unique_code is a property within the Account object

        val query = databaseReference.orderByChild("unique_code").equalTo(unique_code.value)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there's only one matching record, as unique_code should be unique
                    val accountSnapshot = dataSnapshot.children.first()

                    val isSleepValue =
                        accountSnapshot.child("is_sleep").getValue(Int::class.java) ?: 0

                    val newValue = if (isSleepValue == 0) {
                        1
                    } else {
                        0
                    }

                    accountSnapshot.ref.child("is_sleep").setValue(newValue)

                    val message = if (newValue == 1) {
                        "Switched on all contacts following."
                    } else {
                        "Switched off all contacts following."
                    }
                    is_sleep.value = newValue
                } else {
                    Log.d(TAG, "No matching record found for unique_code: ${unique_code.value}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error querying for account", databaseError.toException())
            }
        })
    }


}