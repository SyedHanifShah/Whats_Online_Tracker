package com.interexy.statustracker.presentation.ui.screens.FeedbackScreen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.google.android.datatransport.BuildConfig
import com.interexy.statustracker.R
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.presentation.theme.SilverGray
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    navController: NavHostController,
    isDark: Boolean
) {

    var text by remember { mutableStateOf("") }
    val sendEnable = text.length >= 20
    val context = LocalContext.current
    val robotoFamily = MFontFamily()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(30.dp)
                        .clickable { navController.popBackStack() },
                    painter = painterResource(id = R.drawable.support_icon),
                    contentDescription = "back arrow",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Customer Support",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    ), modifier = Modifier
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.support_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .width(250.dp)
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Hello, how can we\n help you?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                )


            }

            Spacer(modifier = Modifier.height(80.dp))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .background(SilverGray, RoundedCornerShape(8.dp))
                    .height(150.dp)

            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(
                            minHeight = 100.dp
                        ),
                    value = text,
                    maxLines = 6,
                    onValueChange = {
                        text = it
                    },
                    placeholder = {
                        Text(
                            text = "Describe the issue (at least 20 characters)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = robotoFamily.RobotoFamily,
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        color = Color.Black
                    )
                )

            }

        }
        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                sendEmail(
                    context,
                    text
                )
                text = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppColor
            ),
            enabled = sendEnable
        ) {

            Text(
                text = "Send",
                color = Color.White
            )
        }


    }

}


@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun PreviewFB() {
    FeedbackScreen(navController = NavHostController(LocalContext.current), isDark = false)
}


private fun sendEmail(context: Context, body: String) {
    val file = createLogFile(context)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf("hanisoft812@gmail.com"))
        putExtra(
            Intent.EXTRA_SUBJECT, "WhatsOwl Feedback, " +
                    DateFormat.format("dd/MM/yyyy", System.currentTimeMillis())
        )
        putExtra(Intent.EXTRA_TEXT, body)
        putExtra(
            Intent.EXTRA_STREAM,
            file?.let {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    it
                )
            })
        `package` = "com.google.android.gm" // package name for Gmail app
    }
    try {
        context.startActivity(intent)
    } catch (e: java.lang.Exception) {
        Toast.makeText(context, "Got some error", Toast.LENGTH_SHORT).show()
    }
}

private fun createLogFile(context: Context): File? {
    val fileName = "feedback.txt"
    val file = File(context.filesDir, fileName)
    try {
        val outputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write("Smartphone model: ${Build.MODEL}\n")
        outputStreamWriter.write("Android version: ${Build.VERSION.RELEASE}\n")
        outputStreamWriter.write("Application Version: ${BuildConfig.VERSION_NAME}")
        outputStreamWriter.close()
        outputStream.close()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}