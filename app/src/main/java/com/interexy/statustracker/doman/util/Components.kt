package com.interexy.statustracker.doman.util

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.interexy.statustracker.presentation.theme.WhatsAppColor
import kotlinx.coroutines.launch


@Composable
fun ButtonWithProgressBar(
    loading: Boolean, buttonText: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .height(ButtonDefaults.MinHeight)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = WhatsAppColor
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    strokeWidth = 2.dp
                )

            }
            Text(buttonText, style = MaterialTheme.typography.bodyLarge)
        }
    }

}


@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}


@Composable
fun PopupMenu(
    menuItems: List<String>,
    onClickCallbacks: List<() -> Unit>,
    showMenu: Boolean,
    onDismiss: () -> Unit,

) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onDismiss() },
    ) {
        menuItems.forEachIndexed { index, item ->
            DropdownMenuItem(onClick = {
                onDismiss()
                onClickCallbacks[index]()
                Log.d("TAG", "PopupMenu:clicked ")
            }, text = {
                Text(text = item)
            })
        }
    }
}




fun Modifier.shimmerLoadingAnimation(): Modifier {
    return composed {

        val shimmerColors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.3f),
        )

        this.background(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(x = 100f, y = 0.0f),
                end = Offset(x = 400f, y = 270f),
            ),
        )
    }
}



@Composable
fun ComponentCircle(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .background(color = Color.LightGray, shape = CircleShape)
            .size(100.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentSquare(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .height(70.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangle(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .height(150.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangleLineLong(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 200.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangleLineShort(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 100.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}




fun Modifier.shimmerLoadingAnimation(
    isLoadingCompleted: Boolean = true, // <-- New parameter for start/stop.
    isLightModeActive: Boolean = true, // <-- New parameter for display modes.
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier {
    if (isLoadingCompleted) { // <-- Step 1.
        return this
    }
    else {
        return composed {
            // Step 2.
            val shimmerColors = ShimmerAnimationData(isLightMode = isLightModeActive).getColours()
            val transition = rememberInfiniteTransition(label = "")
            val translateAnimation = transition.animateFloat(
                initialValue = 0f,
                targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "Shimmer loading animation",
            )
            this.background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                    end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                ),
            )
        }
    }
}
data class ShimmerAnimationData(
    private val isLightMode: Boolean
) {
    fun getColours(): List<Color> {
        return if (isLightMode) {
            val color = Color.White
            listOf(
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 1.0f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
            )
        } else {
            val color = Color.Black
            listOf(
                color.copy(alpha = 0.0f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.0f),
            )
        }
    }
}



@Composable
fun Hexagon(
    modifier: Modifier = Modifier,
    isFilled:Boolean,
    icon: Int? = null,
    hexagonColor: Color,
    backgroundColor:Color,
    iconTint:Color = Color.White,
    onClick:(()->Unit)? = null,
    shouldAnimateLoadingBar:Boolean = false
) {

    var clickAnimationOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    var canvasSize by remember {
        mutableStateOf(Size.Zero)
    }

    var animationRadius by remember {
        mutableStateOf(0f)
    }

    var animationRotation by remember {
        mutableStateOf(0f)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true){
        if(shouldAnimateLoadingBar){
            animate(
                0f,
                360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        1000,
                        delayMillis = 0,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            ){value, _ ->
                animationRotation = value
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (onClick == null) {
                                return@detectTapGestures
                            }
                            onClick()
                            clickAnimationOffset = offset
                            coroutineScope.launch {
                                animate(
                                    0f, canvasSize.height * 2,
                                    animationSpec = tween(200)
                                ) { value, _ ->
                                    animationRadius = value
                                }
                                animationRadius = 0f
                            }
                        }
                    )
                }
        ){
            val height = size.height
            val width = size.width
            canvasSize = Size(width,height)

            val path = Path().apply{
                moveTo(width/2f,0f)
                lineTo(width, height/4)
                lineTo(width,height/4*3)
                lineTo(width/2,height)
                lineTo(0f,height/4*3)
                lineTo(0f,height/4)
                close()
            }

            if(shouldAnimateLoadingBar){
                clipPath(path){
                    rotate(animationRotation){
                        drawArc(
                            startAngle = 0f,
                            sweepAngle = 150f,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    backgroundColor,
                                    backgroundColor,
                                    hexagonColor.copy(0.5f),
                                    hexagonColor.copy(0.5f),
                                    hexagonColor,
                                    hexagonColor,
                                    hexagonColor
                                )
                            ),
                            useCenter = true,
                            size = canvasSize * 1.1f
                        )
                    }
                }
            }else{
                drawPath(
                    path,
                    color = hexagonColor,
                    style = if(isFilled) Fill else Stroke(
                        width = 1.dp.toPx()
                    )
                )
            }

            clipPath(path){
                drawCircle(
                    Color.White.copy(alpha = 0.2f),
                    radius = animationRadius + 0.1f,
                    center = clickAnimationOffset
                )
            }

        }
        if(icon != null){
            Image(
                painter = painterResource(id = icon),
                contentDescription = "hexagon_icon",
                modifier = Modifier
                    .fillMaxSize(0.5f),
            )
        }
    }


}










