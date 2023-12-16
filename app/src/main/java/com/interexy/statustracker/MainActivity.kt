package com.interexy.statustracker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.interexy.statustracker.data.StatusTrackerDataStore
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.presentation.theme.StatusTrackerTheme
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import com.interexy.statustracker.presentation.theme.WhatsAppDarkColor
import com.interexy.statustracker.presentation.ui.screens.MainScreen.MainScreen
import com.interexy.statustracker.presentation.ui.screens.Settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val dataStore = StatusTrackerDataStore(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FCM", token.toString())
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.saveFCMToken(token)
            }
        })

        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = remember {
                mutableStateOf(false)
            }
            val viewModel: SettingsViewModel by viewModels()
            val unique_code = viewModel.unique_code

            val query: Query = FirebaseDatabase.getInstance().getReference("accounts")
                .orderByChild("unique_code")
                .equalTo(unique_code.value)

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val account = snapshot.getValue(Account::class.java)
                        if (account != null) {
                            val settings = account.settings
                            if (settings != null) {
                                darkTheme.value = settings.darkMode
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", "Error querying for account", databaseError.toException())
                }
            })
            val isDarkTheme = isSystemInDarkTheme()
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    if (darkTheme.value || isDarkTheme) WhatsAppDarkColor.toArgb() else WhatsAppColor.toArgb(),
                    if (darkTheme.value || isDarkTheme) WhatsAppDarkColor.toArgb() else WhatsAppColor.toArgb()
                ),

                navigationBarStyle = SystemBarStyle.light(
                    if (darkTheme.value || isDarkTheme) WhatsAppDarkColor.toArgb() else WhatsAppColor.toArgb(),
                    if (darkTheme.value || isDarkTheme) WhatsAppDarkColor.toArgb() else WhatsAppColor.toArgb()
                )
            )
            StatusTrackerTheme(
                darkTheme = darkTheme.value,
                dynamicColor = false,
            ) {
                MainScreen(isDark = darkTheme.value)
            }
        }
    }
}
