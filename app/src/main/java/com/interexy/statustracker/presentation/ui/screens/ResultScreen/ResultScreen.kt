package com.interexy.statustracker.presentation.ui.screens.ResultScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.interexy.statustracker.R
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.Hexagon
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.doman.util.NavigationScreen
import com.interexy.statustracker.doman.util.OnLifecycleEvent
import com.interexy.statustracker.presentation.theme.MyBlack
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import com.interexy.statustracker.presentation.theme.WhatsAppDarkColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    viewModel: ResultViewModel = hiltViewModel(),
    OnNavigate:(route: String)->Unit
) {
    val robotoFamily = MFontFamily()
    val TAG = "RESULTVIEWMODEL"
    val unique_code = viewModel.unique_code
    val database = FirebaseDatabase.getInstance()
    var loginSuccessful  = viewModel.loginSuccessful
    val timeUp = viewModel.timeUp

    val query: Query = FirebaseDatabase.getInstance().getReference("accounts")
        .orderByChild("unique_code")
        .equalTo(unique_code)

    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                val account = snapshot.getValue(Account::class.java)
                if (account != null) {
                    Log.d(TAG, "Account found: $account")
                    if (account.status == "Awaiting action"){
                        viewModel.loginSuccessful = true
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.dataStore.saveUserLogin(true)
                        }
                    }
                    // Perform your action here
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "Error querying for account", databaseError.toException())
        }
    })



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp, horizontal = 30.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.9f)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            var title = "Please wait..."
            if (timeUp && !loginSuccessful) {
                title = "Oops, \nthe time is over."
            }else if (loginSuccessful) {
                    title = "Congratulations! You have successfully logged in."
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        lineHeight = 40.sp
                    ),
                )
            if (timeUp && !loginSuccessful) {

                Spacer(modifier = Modifier.height(40.dp))
                Image(
                    painter = painterResource(id = R.drawable.try_again),
                    contentDescription = "Scan Whatsapp",
                    modifier = Modifier
                        .width(300.dp)
                        .height(700.dp),
                    contentScale = ContentScale.Fit
                )
            }else if (loginSuccessful) {
                    Image(
                        painter = painterResource(id = R.drawable.connected),
                        contentDescription = "Scan Whatsapp",
                        modifier = Modifier
                            .width(300.dp)
                            .height(700.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Spacer(modifier = Modifier.height(60.dp))
                    Hexagon(
                        isFilled = false,
                        hexagonColor = WhatsAppDarkColor,
                        backgroundColor = WhatsAppColor,
                        modifier = Modifier
                            .height(230.dp)
                            .width(200.dp),
                        icon = R.drawable.logo1,
                        shouldAnimateLoadingBar = true
                    )
                Spacer(modifier = Modifier.height(60.dp))

            }

                Spacer(modifier = Modifier.height(15.dp))


                Column {
                    var message = "Currently in the process of scanning. Thank you for your patience."
                    if (timeUp && !loginSuccessful) {
                        message = ""
                    }else if (loginSuccessful) {
                        message = "Successfully Logged In"
                    }

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )

                    )
                }
            }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.weight(0.3f)
        ) {
            var buttonText = "Waiting..."
            var buttonColor = MyBlack
            if (timeUp && !loginSuccessful) {
                buttonText = "Try Again"
                buttonColor = WhatsAppColor
            }else if (loginSuccessful){
                buttonText = "Home"
                buttonColor = WhatsAppColor
            }
            Button(
                enabled = loginSuccessful || timeUp,
                onClick = {
                    if (timeUp && !loginSuccessful) {
                        OnNavigate(NavigationScreen.PIN.route)
                    }else if (loginSuccessful){
                        OnNavigate(NavigationScreen.HomeScreen.route)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText, style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}