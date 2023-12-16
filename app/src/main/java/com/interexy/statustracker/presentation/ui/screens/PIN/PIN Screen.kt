package com.interexy.statustracker.presentation.ui.screens.PIN

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.interexy.statustracker.data.remote.StartSessionPayload
import com.interexy.statustracker.doman.util.ButtonWithProgressBar
import com.interexy.statustracker.doman.util.MFontFamily
import com.interexy.statustracker.doman.util.UiEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PINScreen(
    viewModel: PINViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val (pinValue, onPinValueChange) = remember { mutableStateOf("") }
    val robotoFamily = MFontFamily()
    val context = LocalContext.current
    val loading = viewModel.loadingProgressBar
    val snackbarHostState = remember { SnackbarHostState() }


    val navigateTo by viewModel.navigateTo.observeAsState()
    LaunchedEffect(navigateTo) {  // The block inside LaunchedEffect will only get executed when navigateTo changes
        if (navigateTo != null) {
            navController.popBackStack()
            navController.navigate(navigateTo!!)
            viewModel.onNavigateTo(null)  // This won't trigger a recomposition of the LaunchedEffect block
        }
    }



    LaunchedEffect(key1 = true) {

        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message
                    )
                }
//                is UiEvent.Navigate -> onNavigate(uiEvent)
                else -> {}
            }
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        it
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp, horizontal = 30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(0.9f)
            ) {

                Text(
                    text = "Verify Your \nDevice",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontSize = 40.sp
                    ),
                )

                Text(
                    text = "Access the provided web address from your secondary device and input the code displayed on the screen into the box below.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp
                    )

                )
                Text(
                    text = "http://188.166.179.4/session/gettoken",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = robotoFamily.RobotoFamily,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Blue,
                )

                Column {
                    Text(
                        text = "Input the 6 digit code",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = robotoFamily.RobotoFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp
                        ),
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    PinView(
                        pinText = pinValue,
                        onPinTextChange = onPinValueChange,
                        type = PIN_VIEW_TYPE_BORDER,
                        digitCount = 6
                    )
                }
            }




            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(0.3f)
            ) {

                ButtonWithProgressBar(buttonText = "Next", loading = loading) {
                    if (pinValue.length == 6) {
                        viewModel.startSession(
                            StartSessionPayload(
                                token = pinValue
                            )
                        )
                    } else {
                        Toast.makeText(context, "Please Enter correct Pin code", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }

        }

    }

}


const val PIN_VIEW_TYPE_UNDERLINE = 0
const val PIN_VIEW_TYPE_BORDER = 1

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinView(
    pinText: String,
    onPinTextChange: (String) -> Unit,
    digitColor: Color = MaterialTheme.colorScheme.onBackground,
    digitSize: TextUnit = 30.sp,
    containerSize: Dp = digitSize.value.dp * 2,
    digitCount: Int = 6,
    type: Int = PIN_VIEW_TYPE_UNDERLINE,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(value = pinText,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.matches("[a-zA-Z0-9]*".toRegex())) {
                onPinTextChange(newValue)
                if (newValue.length == 6) {
                    keyboardController?.hide()
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(digitCount) { index ->
                    DigitView(index, pinText, digitColor, digitSize, containerSize, type = type)
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        })
}

@Composable
private fun DigitView(
    index: Int,
    pinText: String,
    digitColor: Color,
    digitSize: TextUnit,
    containerSize: Dp,
    type: Int = PIN_VIEW_TYPE_UNDERLINE,
) {
    val modifier = if (type == PIN_VIEW_TYPE_BORDER) {
        Modifier
    } else Modifier.width(containerSize)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(60.dp)
            .width(40.dp)
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = if (index >= pinText.length) "" else pinText[index].toString(),
            color = digitColor,
            modifier = modifier,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = digitSize,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
        if (type == PIN_VIEW_TYPE_UNDERLINE) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .background(digitColor)
                    .height(1.dp)
                    .width(containerSize)
            )
        }
    }
}
