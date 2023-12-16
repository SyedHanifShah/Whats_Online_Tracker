package com.interexy.statustracker.presentation.ui.screens.Home

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.messaging
import com.interexy.statustracker.R
import com.interexy.statustracker.doman.util.Account
import com.interexy.statustracker.doman.util.ComponentRectangle
import com.interexy.statustracker.doman.util.ComponentRectangleLineLong
import com.interexy.statustracker.doman.util.ComponentRectangleLineShort
import com.interexy.statustracker.doman.util.ComponentSquare
import com.interexy.statustracker.doman.util.FirebaseMessagingNotificationPermissionDialog
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.doman.util.PopupMenu
import com.interexy.statustracker.doman.util.RecentActivity
import com.interexy.statustracker.doman.util.UiEvent
import com.interexy.statustracker.presentation.theme.BackgroundDarkColor
import com.interexy.statustracker.presentation.theme.BackgroundDarkThemeLightColor
import com.interexy.statustracker.presentation.theme.SilverGray
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import com.interexy.statustracker.presentation.theme.WhatsAppDarkColor
import com.interexy.statustracker.presentation.theme.WhatsAppSecondDarkColor
import com.interexy.statustracker.presentation.theme.lightGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isDark: Boolean,
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uniqueCode = viewModel.unique_code
    val TAG = "HOMESCREEN"
    val robotoFamily = MFontFamily()
    val isDarkTheme = isSystemInDarkTheme()

    val showNotificationDialog = remember { mutableStateOf(false) }

    // Android 13 Api 33 - runtime notification permission has been added
    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    )
    if (showNotificationDialog.value) FirebaseMessagingNotificationPermissionDialog(
        showNotificationDialog = showNotificationDialog,
        notificationPermissionState = notificationPermissionState
    )

    LaunchedEffect(key1 = Unit) {
        if (notificationPermissionState.status.isGranted ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            Firebase.messaging.subscribeToTopic("Tutorial")
        } else showNotificationDialog.value = true
    }




    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message
                    )
                }

                is UiEvent.Navigate -> onNavigate(uiEvent)
                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = true) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewModel._currentFollowing.collect { mCurrentFollowing ->
                    if (mCurrentFollowing != null) {
                        viewModel.currentFollowing.value = mCurrentFollowing
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    LaunchedEffect(key1 = true) {
        val query: Query = FirebaseDatabase.getInstance().getReference("accounts")
            .orderByChild("unique_code")
            .equalTo(uniqueCode.value)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null) {
                        viewModel.userData.value = account
                        viewModel.selectedContactList.value = account.contact_list.filter {
                            it.contact_active == "yes"
                        }
                        viewModel.recentActivitiesList.value = account.recent_activities
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(
                    TAG,
                    "Error querying for account",
                    databaseError.toException()
                )
            }
        })
    }







    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        ),
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkTheme || isDark) BackgroundDarkColor else Color.White
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        painter = painterResource(id = R.drawable.icon_home),
                        contentDescription = "Navigate back"
                    )
                }
            )
        }
    ) { it ->
        it
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 65.dp, bottom = 20.dp)
        ) {
            if (viewModel.userData.value == null) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Column {
                        ComponentRectangle(
                            isLoadingCompleted = false, !isDark || !isDarkTheme)
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.padding(20.dp))


                    ComponentSquare(
                        isLoadingCompleted = false, !isDark || !isDarkTheme)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
                    Spacer(modifier = Modifier.padding(4.dp))

                }
            }

            viewModel.userData.value?.let { account ->
                UserCardView(
                    account,
                    onRemovedAccountClick = {
                        viewModel.onRemoveAccount()
                    },
                    onFollowingClick = {
                        viewModel.onNavigateToContactsScreen()
                    },
                    viewModel = viewModel,
                    status = account.status,
                    isDark = isDark
                )


                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Recent Activities", fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = robotoFamily.RobotoFamily,
                                fontWeight = FontWeight.ExtraLight,
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(content = {
                            items(items = viewModel.recentActivitiesList.value) {
                                RecentActivityCard(it, isDark = isDark)
                            }
                        })
                    }

                    if (account.status == "Disconnected") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .offset(y = (400).dp)
                                .fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.synchronized() },
                                modifier = Modifier.width(200.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WhatsAppColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = "Synchronized")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecentActivityCard(activity: RecentActivity, isDark: Boolean) {
    val robotoFamily = MFontFamily()
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(
                color = if (isDark || isDarkTheme) BackgroundDarkThemeLightColor else SilverGray,
                shape = RoundedCornerShape(8.dp)
            )
            .height(70.dp)
            .padding(start = 10.dp, end = 10.dp)

    ) {

        if (activity.account_online == 0) {
            Icon(
                painter = painterResource(id = R.drawable.icon_offline),
                contentDescription = "User Offline",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape),
                tint = Color.Red
            )

        } else {
            Icon(
                painter = painterResource(id = R.drawable.icon_online),
                contentDescription = "user Online",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape),
                tint = Color.Green
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        ) {

            val status = if (activity.account_online == 0) {
                "offline"
            } else {
                "online"
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    fontFamily = robotoFamily.RobotoFamily,
                                )
                            ) {
                                append(activity.name.take(13))
                            }
                            if (activity.name.length > 13) {
                                append("..")
                            }
                            append(" is become $status")
                        },


                        modifier = Modifier, // add your modifier here if needed
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 13.sp,
                            fontSize = 15.sp,
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "+${activity.contact_number}",
                            fontSize = 15.sp,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = robotoFamily.RobotoFamily,
                                fontWeight = FontWeight.ExtraLight
                            )
                        )
                    }



                    Box(
                        contentAlignment = Alignment.BottomEnd, modifier = Modifier
                            .weight(0.3f)
                            .padding(bottom = 5.dp)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = getTimeFromDateAndTime(activity.date_time),
                            fontSize = 13.sp,
                            color = if (activity.account_online == 0) Color.Red else Color.Green,
                            modifier = Modifier
                                .background(
                                    color = lightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = robotoFamily.RobotoFamily,
                                fontWeight = FontWeight.ExtraLight
                            )
                        )
                    }
                }
            }

        }


    }

}


@Composable
fun UserCardView(
    account: Account,
    onRemovedAccountClick: () -> Unit,
    onFollowingClick: () -> Unit,
    viewModel: HomeViewModel,
    status: String,
    isDark: Boolean
) {
    var checked by remember {
        mutableStateOf(viewModel.is_sleep.value != 0)
    }
    val robotoFamily = MFontFamily()

    val openSettingsDialog = remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()


    Column(
        modifier = Modifier
            .height(if (status == "Disconnected") 70.dp else 120.dp)
            .fillMaxWidth()
            .background(
                color = if (status == "Disconnected") Color.Red else if (isDark || isDarkTheme) WhatsAppDarkColor else WhatsAppColor,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {

                if (account.profile_pic == "") {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                } else {
                    Image(
                        painter = rememberImagePainter(data = account.profile_pic, builder = {
                            transformations(CircleCropTransformation())
                            placeholder(Color.Gray.toArgb())
                        }),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Column(verticalArrangement = Arrangement.SpaceAround) {
                    Text(
                        text = account.name, color = Color.White, fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                    Text(
                        text = "Status: ${account.status}", color = Color.White, fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    )
                    Text(
                        text = "Ends At: ${account.end_date_time}",
                        color = Color.White,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    )

                }
            }

            Column(verticalArrangement = Arrangement.Top) {
                Icon(painter = painterResource(id = R.drawable.icon_settings),
                    contentDescription = "settings",
                    modifier = Modifier.clickable {
                        openSettingsDialog.value = !openSettingsDialog.value
                    })
                PopupMenu(
                    menuItems = listOf("Removed"),
                    onClickCallbacks = listOf {
                        onRemovedAccountClick()
                    },
                    showMenu = openSettingsDialog.value,
                    onDismiss = { openSettingsDialog.value = false },

                    )

            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (status != "Disconnected") {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = if (isDark || isDarkTheme) WhatsAppSecondDarkColor else WhatsAppDarkColor, shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 5.dp, horizontal = 10.dp)
                        .clickable {
                            onFollowingClick()
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.followers),
                        contentDescription = "Following",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Following: ${viewModel.currentFollowing.value}/2",
                        color = Color.White,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sleep", color = Color.White, fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    )
                    Switch(checked = checked, onCheckedChange = {
                        viewModel.changeSleepMode()
                        checked = it
                    },
                        thumbContent = {
                            Icon(
                                imageVector = if (checked) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                tint = if (checked) Color.Green else Color.Red

                            )
                        }
                    )
                }
            }

        }

    }


}


fun getTimeFromDateAndTime(dateAndTime: String): String {
    val inputFormat = SimpleDateFormat("E dd-MM-yyyy, hh:mm:ss a", Locale.US)
    val date: Date = inputFormat.parse(dateAndTime) ?: Date()

    // Format the Date object into the desired output string
    val outputFormat = SimpleDateFormat("E, hh:mm:ss a", Locale.US)
    return outputFormat.format(date)

}




