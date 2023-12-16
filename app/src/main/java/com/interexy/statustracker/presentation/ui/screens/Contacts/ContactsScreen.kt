package com.interexy.statustracker.presentation.ui.screens.Contacts

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.interexy.statustracker.R
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.doman.util.ComponentRectangle
import com.interexy.statustracker.doman.util.ComponentRectangleLineLong
import com.interexy.statustracker.doman.util.ComponentSquare
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.doman.util.MyContacts
import com.interexy.statustracker.presentation.theme.BackgroundDarkColor
import com.interexy.statustracker.presentation.theme.BackgroundDarkThemeLightColor
import com.interexy.statustracker.presentation.theme.SilverGray
import com.interexy.statustracker.presentation.theme.WhatsAppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel(),
    navController: NavHostController,
    isDark: Boolean
) {
    val robotoFamily = MFontFamily()
    val isDarkTheme = isSystemInDarkTheme()
    val uniqueCode = viewModel.unique_code

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Contacts",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontFamily = robotoFamily.RobotoFamily,
                        fontSize = 18.sp
                    )
                )
            },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkTheme || isDark) BackgroundDarkColor else Color.White
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .clickable {
                                navController.navigateUp()
                            },
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Navigate back"
                    )
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                viewModel.syncContacts(token = StartSessionPayload(token = uniqueCode.value))
                            },
                        painter = painterResource(id = R.drawable.icon_sync),
                        contentDescription = "Sync Contact"
                    )
                }
            )
        }
    ) {
        it
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 70.dp)
                .fillMaxWidth()
        ) {
            if (viewModel.userData.value == null) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Column {
                        ComponentRectangleLineLong(isLoadingCompleted = false, true)
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.padding(20.dp))


                    ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
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
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Total: ${viewModel.userData.value?.contact_list?.size}",
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = robotoFamily.RobotoFamily,
                        )
                    )
                    Text(
                        text = "Following: ${viewModel.currentFollowing}/2", fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = robotoFamily.RobotoFamily
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.loading.value) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
                        Spacer(modifier = Modifier.padding(4.dp))
                        ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)

                        Spacer(modifier = Modifier.padding(20.dp))


                        ComponentSquare(isLoadingCompleted = false, !isDark || !isDarkTheme)
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
                } else {
                    LazyColumn {
                        viewModel.userData.value?.contact_list?.let { myContactList ->
                            items(items = myContactList) {
                                ContactView(it, viewModel, isDark = isDark)
                            }
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun ContactView(contacts: MyContacts, viewModel: ContactsViewModel, isDark: Boolean) {
    val robotoFamily = MFontFamily()
    val isContactSelected =
        viewModel.selectedContactList.value.firstOrNull { it.contact_number == contacts.contact_number && it.name == contacts.name }
    val isSelected = remember {
        mutableStateOf(isContactSelected != null)
    }
    val isDarkTheme = isSystemInDarkTheme()


    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(
                color = if (isDark || isDarkTheme) BackgroundDarkThemeLightColor else SilverGray,
                shape = RoundedCornerShape(8.dp)
            )
            .height(70.dp)
            .padding(start = 10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f)
        ) {
            if (contacts.profile_pic == "") {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

            } else {
                Image(
                    painter = rememberImagePainter(
                        data = contacts.profile_pic,
                        builder = {
                            transformations(CircleCropTransformation())
                            placeholder(Color.Gray.toArgb())
                        }
                    ),
                    contentDescription = "Contact picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(5.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = contacts.name,
                    fontSize = 14.sp, color = Color.Black,
                    lineHeight = 15.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
                Text(
                    text = "+${contacts.contact_number}", fontSize = 14.sp, color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            }

        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.2f)) {

            Switch(checked = isSelected.value, onCheckedChange = {
                if (viewModel.selectedContactList.value.size < 2) {
                    if (isContactSelected == null) {
                        viewModel.updateContactStatus(
                            value = "yes",
                            contactName = contacts.name,
                            contactNumber = contacts.contact_number
                        )
                    } else {
                        viewModel.updateContactStatus(
                            value = "no",
                            contactName = contacts.name,
                            contactNumber = contacts.contact_number
                        )

                    }
                    isSelected.value = it
                } else if (isContactSelected != null) {
                    viewModel.updateContactStatus(
                        value = "no",
                        contactName = contacts.name,
                        contactNumber = contacts.contact_number
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Limit reached: Only 2 contacts allowed.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            },
                thumbContent = {
                    Icon(
                        imageVector = if (isSelected.value) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        tint = if (isSelected.value) Color.Green else Color.Red

                    )
                }
            )
        }

    }

}

@Preview(device = "id:pixel_5")
@Composable
fun ContactPreview() {
    ContactsScreen(navController = rememberNavController(), isDark = false)
}