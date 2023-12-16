package com.interexy.statustracker.presentation.ui.screens.LaunchWhatsApp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.PropertyName
import com.interexy.statustracker.R
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.presentation.theme.WhatsAppColor

@Composable
fun LaunchWhatsAppScreen(onNavigate:()-> Unit) {
    val robotoFamily = MFontFamily()
    val context = LocalContext.current
    val packageManager = LocalContext.current.packageManager


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
            Text(
                text = "Scan the QR code available on the website.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = robotoFamily.RobotoFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    lineHeight = 40.sp
                ),
            )

            Image(
                painter = painterResource(id = R.drawable.scan_whatsapp),
                contentDescription = "Scan Whatsapp",
               modifier = Modifier
                   .width(200.dp)
                   .height(300.dp),
                contentScale = ContentScale.Fit
            )


            Spacer(modifier = Modifier.height(15.dp))


            Column {
                Text(
                    text = "Open Whatsapp > Linked Devices > Link Device then aim to the QR code on thw website",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )

                )


            }
        }




        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.weight(0.1f)
        ) {


            Button(
                onClick = {
                    val appPackageName = "com.whatsapp"
                    val pm = context.packageManager
                    var isAppInstalled: Boolean
                    try {
                        pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES)
                        isAppInstalled = true
                    } catch (e: PackageManager.NameNotFoundException) {
                        isAppInstalled = false
                    }
                    if (isAppInstalled) {
                        val intent = pm.getLaunchIntentForPackage(appPackageName)
                        context.startActivity(intent)
                        onNavigate()
                    } else {
                        Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Launch WhatsApp", style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}


@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun LaunchWhatsAppScreenPreview(){
    LaunchWhatsAppScreen(onNavigate = {})
}
