package com.surovtsev.screenfoodmenu

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.common.theme.PrimaryColor
import com.surovtsev.common.viewmodels.FoodMenuViewModel
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun FoodMenuScreen(
    viewModel: FoodMenuViewModel,
    navController: NavController,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val localDensity = LocalDensity.current

    Box(
        modifier = Modifier
            .padding(1.35.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .background(color = Color.Black)
            .onGloballyPositioned { coordinates -> size = coordinates.size },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            Circle(size)
            Controls(size, localDensity, viewModel)
        }
    }
}

@Composable
fun Circle(
    size: IntSize,
) {
    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
        val stroke = 20f
        val radius = (size.width.toFloat() - stroke) / 2

        drawCircle(
            color = PrimaryColor, radius = radius, style = Stroke(
                width = stroke,
            )
        )
    })
}

@Composable
fun Controls(
    size: IntSize,
    density: Density,
    viewModel: FoodMenuViewModel,
) {
    with(density) {
        val painterResources = viewModel.items.map {
            painterResource(id = it)
        }

        var animated by remember { mutableStateOf(false) }
        val progress by animateIntAsState(
            targetValue = if (animated) 100 else 0,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 500,
                easing = LinearEasing
            )
        )
        LaunchedEffect(animated) {
            animated = true
        }

        val ctx = LocalContext.current

        val currX = remember { mutableStateOf(size.width.toFloat()) }
        val currY = remember { mutableStateOf(size.height.toFloat() / 2) }
        val prevX = remember { mutableStateOf(0f) }
        val prevY = remember { mutableStateOf(9f) }
        val prevAngle = remember { mutableStateOf(0f) }
        val currAngle = remember { mutableStateOf(0f) }
        val diffAngle = remember { mutableStateOf(0f) }
        val commitedDiffAngle = remember { mutableStateOf(0f) }
        val center = size.width / 2

        fun CalculateAngle(prevValue: Float, x: Float, y: Float): Float {
            val dx = x - center
            val dy = y - center
            val l = sqrt(dx * dx + dy * dy)
            if (l < center / 10) {
                return prevValue
            }
            val nX = dx / l
            val aC = acos(nX)
            if (dy > 0) {
                return aC
            } else {
                return 2 * Math.PI.toFloat() - aC
            }
        }

        fun RadToGrad(a: Float) = a / Math.PI.toFloat() * 180
        fun NormalGrad(a: Float) = (a + 540) % 360 - 180
        fun RadToGradString(a: Float) = "%.2f".format(NormalGrad(RadToGrad(a)))
        fun NormalRad(a: Float): Float {
            var res = a
            val piFloat = Math.PI.toFloat()
            while (res < -1 * piFloat) {
                res += piFloat
            }
            while (res > piFloat) {
                res += piFloat
            }
            return res
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = { offset ->
                        prevX.value = offset.x
                        prevY.value = offset.y
                        currX.value = offset.x
                        currY.value = offset.y
                        prevAngle.value = CalculateAngle(
                            prevAngle.value,
                            currX.value,
                            currY.value,
                        )
                    }, onDrag = { _: PointerInputChange, dragAmount: Offset ->
                        currX.value = (currX.value + dragAmount.x).coerceIn(
                            0f, size.width.toFloat() - 50.dp.toPx()
                        )
                        currY.value = (currY.value + dragAmount.y).coerceIn(
                            0f, size.height.toFloat() - 50.dp.toPx()
                        )
                        currAngle.value = CalculateAngle(
                            currAngle.value,
                            currX.value,
                            currY.value,
                        )
                        diffAngle.value = currAngle.value - prevAngle.value

                    }, onDragCancel = {

                    }, onDragEnd = {
                        commitedDiffAngle.value = commitedDiffAngle.value + diffAngle.value
                        diffAngle.value = 0f
                    })
                },
        ) {
            if (true) {
                val diameterDp = size.height.toDp()
                val radiusDp = diameterDp / 2

                val iconSideRate = 0.1f
                val dxRate = 0.1f

                val centerRadiusRate = 0.8f
                val iconSide = diameterDp * iconSideRate

                for (idx in 0 until 12) {
                    val angle = 2 * Math.PI / 12 * idx + commitedDiffAngle.value + diffAngle.value
                    val x = radiusDp * (1f + centerRadiusRate * cos(angle).toFloat())
                    val y = radiusDp * (1f + centerRadiusRate * sin(angle).toFloat())

                    if (true) {
                        if (idx < painterResources.size) {
                            Icon(
                                painter = painterResources[idx],
                                contentDescription = "Localized description",
                                modifier = Modifier
                                    .size(iconSide, iconSide)
                                    .offset(x - iconSide / 2, y - iconSide / 2)
                                    .clickable {
                                        Toast
                                            .makeText(
                                                ctx,
                                                "${viewModel.items[idx]}",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    },

                                tint = Color.White,
                            )
                        }
                    }
                    if (false) {
                        if (idx % 2 == 0) {
                            Icon(
                                painter = painterResource(id = com.surovtsev.common.R.drawable.microphone),
                                contentDescription = "Localized description",
                                modifier = Modifier
                                    .size(iconSide, iconSide)
                                    .offset(x - iconSide / 2, y - iconSide / 2),
                                tint = PrimaryColor,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .offset(x - iconSide / 2, y - iconSide / 2)
                                    .size(iconSide)
                                    .border(1.dp, Color.Black),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "$idx")
                            }
                        }
                    }

                    if (true) {
                        val iconSize = size.width.toDp().div(8)
                        val dx = size.width.toDp() / 30
                        val dy = (size.width.toDp() - iconSize).div(2)
                        Icon(
                            painter = painterResource(id = com.surovtsev.common.R.drawable.microphone),
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(iconSize, iconSize)
                                .offset(dx, dy),
                            tint = PrimaryColor,
                        )
                    }
                }

                if (false) {
                    Text(
                        text = RadToGradString(prevAngle.value) + " ->" + RadToGradString(currAngle.value) + ": " + RadToGradString(
                            commitedDiffAngle.value
                        ) + " " + RadToGradString(diffAngle.value), Modifier.align(Alignment.Center)
                    )
                    Box(
                        Modifier
                            .offset {
                                IntOffset(
                                    currX.value.roundToInt(), currY.value.roundToInt()
                                )
                            }
                            .background(Color.Blue)
                            .size(50.dp))

                    Box(modifier = Modifier
                        .offset {
                            IntOffset(
                                prevX.value.roundToInt(),
                                prevY.value.roundToInt(),
                            )
                        }
                        .background(Color.Red)
                        .size(50.dp))
                }

                if (true) {
                    Text(
                        text = "${progress}",
                        Modifier
                            .align(Alignment.Center)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}
