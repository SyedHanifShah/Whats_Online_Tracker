package com.interexy.statustracker.presentation.ui.screens.ResultScreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.interexy.statustracker.data.StatusTrackerDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    @ApplicationContext application: Context
) : ViewModel() {


    val dataStore = StatusTrackerDataStore(application)
    private val _unique_code = dataStore.getUniqueCode
    var unique_code by mutableStateOf("")
    var timeUp by mutableStateOf(false)
    var loginSuccessful by mutableStateOf(false)
    val TAG = "RESULTVIEWMODEL"



    init {
        startTimer()
        viewModelScope.launch {
            try {
                _unique_code.collect {
                    if (it != null) {
                        unique_code = it
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }



    fun startTimer() {
        viewModelScope.launch {
            delay(60000) // Delay for one minute
            timeUp = true
            if (!loginSuccessful){
                val databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
                val query = databaseReference.orderByChild("unique_code").equalTo(unique_code)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming there's only one matching record, as unique_code should be unique
                            val accountSnapshot = dataSnapshot.children.first()
                            accountSnapshot.ref.removeValue()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "Error querying for account", databaseError.toException())
                    }
                })

            }
        }
    }

}