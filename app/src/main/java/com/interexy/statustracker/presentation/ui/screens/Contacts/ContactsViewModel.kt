package com.interexy.statustracker.presentation.ui.screens.Contacts

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.doman.repository.StatusTrackerRepository
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.MyContacts
import com.interexy.statustracker.doman.util.Resource
import com.interexy.statustracker.doman.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ContactsViewModel @Inject constructor(
   @ApplicationContext context: Context,
   private  val repository: StatusTrackerRepository
):ViewModel() {

    val TAG = "CONTACTSVIEWMODEL"
    val dataStore = StatusTrackerDataStore(context)
    val _unique_code = dataStore.getUniqueCode
    val unique_code = mutableStateOf("")
    val userData = mutableStateOf<Account?>(null)
    var contactList = mutableStateOf<List<MyContacts>>(emptyList())
    var loading = mutableStateOf(false)
    var selectedContactList = mutableStateOf<List<MyContacts>>(emptyList())
    var currentFollowing by mutableStateOf(0)


    init {
        viewModelScope.launch {
            try {
                _unique_code.collect {
                    if (it != null) {
                        unique_code.value = it

                        val account = fetchAccountData(it)
                        account?.let {
                            userData.value = it
                            contactList.value = it.contact_list

                            selectedContactList.value = it.contact_list.filter { contact ->contact.contact_active == "yes" }
                            viewModelScope.launch {
                                dataStore.saveCurrentFollowing(selectedContactList.value.size.toString())
                                currentFollowing = selectedContactList.value.size
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




    fun updateContactStatus(value:String, contactName:String, contactNumber:String){
        loading.value = true
        val databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.orderByChild("unique_code").equalTo(unique_code.value)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (accountSnapshot in dataSnapshot.children) {
                        val contactListSnapshot = accountSnapshot.child("contact_list")
                        for (contactSnapshot in contactListSnapshot.children) {
                            val contact = contactSnapshot.getValue(MyContacts::class.java)
                            if (contact != null) {
                                if (contact.name.trim() == contactName.trim() && contact.contact_number.trim() == contactNumber.trim()) {
                                    // Update the contact_active field
                                    contactSnapshot.ref.child("contact_active").setValue(value)
                                        refreshContactList()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Error querying for account", databaseError.toException())
                }
            })

    }


    fun refreshContactList(){
        viewModelScope.launch {
            try {
                _unique_code.collect {
                    if (it != null) {
                        unique_code.value = it

                        val account = fetchAccountData(it)
                        account?.let {
                            userData.value = it
                            contactList.value = it.contact_list

                            selectedContactList.value = it.contact_list.filter { contact ->contact.contact_active == "yes" }

                            viewModelScope.launch {
                          dataStore.saveCurrentFollowing(selectedContactList.value.size.toString())
                             currentFollowing = selectedContactList.value.size
                         }
                            loading.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private suspend fun fetchAccountData(uniqueCode: String): Account? = suspendCoroutine { continuation ->
        val query: Query = FirebaseDatabase.getInstance().getReference("accounts")
            .orderByChild("unique_code")
            .equalTo(uniqueCode)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null) {
                        continuation.resume(account)
                        return
                    }
                }
                continuation.resume(null)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Error querying for account", databaseError.toException())
                continuation.resume(null)
            }
        })
    }



    fun syncContacts(token: StartSessionPayload) {
        loading.value = true
        viewModelScope.launch {
            when (val result = repository.syncContacts(token)) {
                is Resource.Success -> {
                    Log.d(TAG, "startSession:${result.data?.status} ")
                    if (result.data?.status == true) {
                        loading.value = false
                    } else {
                        loading.value = false
                    }
                }

                is Resource.Error -> {
                }
            }
        }
    }




}