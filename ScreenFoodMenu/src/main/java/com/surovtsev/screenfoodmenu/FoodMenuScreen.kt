package com.surovtsev.screenfoodmenu

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.surovtsev.common.theme.GrayColor
import com.surovtsev.common.theme.PrimaryColor
import com.surovtsev.common.viewmodels.FoodMenuViewModel
import glm_.vec2.Vec2
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.min
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
            painterResource(id = it.id)
        }

        var animated by remember { mutableStateOf(false) }
        val progress by animateIntAsState(
            targetValue = if (animated) 100 else 0, animationSpec = tween(
                durationMillis = 1000, delayMillis = 500, easing = LinearEasing
            ), label = ""
        )
        LaunchedEffect(animated) {
            animated = true
        }

        val ctx = LocalContext.current

        val currX = remember { mutableFloatStateOf(size.width.toFloat()) }
        val currY = remember { mutableFloatStateOf(size.height.toFloat() / 2) }
        val prevX = remember { mutableFloatStateOf(0f) }
        val prevY = remember { mutableFloatStateOf(9f) }
        val prevAngle = remember { mutableFloatStateOf(0f) }
        val currAngle = remember { mutableFloatStateOf(0f) }
        val diffAngle = remember { mutableFloatStateOf(0f) }
        val commitedDiffAngle =
            remember { mutableFloatStateOf(-2 * Math.PI.toFloat() * (viewModel.items.count() - 1) / 12 / 2) }
        val center = size.width / 2

        fun calculateAngle(prevValue: Float, x: Float, y: Float): Float {
            val dx = x - center
            val dy = y - center
            val l = sqrt(dx * dx + dy * dy)
            if (l < center / 10) {
                return prevValue
            }
            val nX = dx / l
            val aC = acos(nX)
            return if (dy > 0) {
                aC
            } else {
                2 * Math.PI.toFloat() - aC
            }
        }

        fun radToGrad(a: Float) = a / Math.PI.toFloat() * 180
        fun normalGrad(a: Float) = (a + 540) % 360 - 180
        fun radToGradString(a: Float) = "%.2f".format(normalGrad(radToGrad(a)))
        fun normalRad(a: Float): Float {
            var res = a
            val doublePiFloat = Math.PI.toFloat() * 2
            while (res < -1 * doublePiFloat) {
                res += doublePiFloat
            }
            while (res > doublePiFloat) {
                res -= doublePiFloat
            }
            return res
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = { offset ->
                        prevX.floatValue = offset.x
                        prevY.floatValue = offset.y
                        currX.floatValue = offset.x
                        currY.floatValue = offset.y
                        prevAngle.floatValue = calculateAngle(
                            prevAngle.floatValue,
                            currX.floatValue,
                            currY.floatValue,
                        )
                    }, onDrag = { _: PointerInputChange, dragAmount: Offset ->
                        currX.floatValue = (currX.floatValue + dragAmount.x).coerceIn(
                            0f, size.width.toFloat() - 50.dp.toPx()
                        )
                        currY.floatValue = (currY.floatValue + dragAmount.y).coerceIn(
                            0f, size.height.toFloat() - 50.dp.toPx()
                        )
                        currAngle.floatValue = calculateAngle(
                            currAngle.floatValue,
                            currX.floatValue,
                            currY.floatValue,
                        )
                        diffAngle.floatValue = currAngle.floatValue - prevAngle.floatValue

                    }, onDragCancel = {

                    }, onDragEnd = {
                        commitedDiffAngle.floatValue =
                            normalRad(commitedDiffAngle.floatValue + diffAngle.floatValue)
                        diffAngle.floatValue = 0f
                    })
                },
        ) {
            if (true) {
                viewModel.radius = size.height.toFloat() / 2
                viewModel.angle = commitedDiffAngle.floatValue + diffAngle.floatValue
                viewModel.progress = progress

                viewModel.updateCoordinates()

                val iconSide = viewModel.iconSide.toDp()
                for (idx in 0 until min(12, viewModel.items.count())) {
                    val item = viewModel.items[idx]
                    val c = item.center
                    Icon(
                        painter = painterResources[idx],
                        contentDescription = "Localized description",
                        modifier = Modifier
                            .size(iconSide, iconSide)
                            .offset(c.x.toDp() - iconSide / 2, c.y.toDp() - iconSide / 2)
                            //.background(color = Color(0xFFA3A3A3))
                            .rotate(radToGrad(item.imageCorrectionAngle + item.angle))
                            .clickable {
                                Toast
                                    .makeText(
                                        ctx, "${viewModel.items[idx]}", Toast.LENGTH_SHORT
                                    )
                                    .show()
                            },
                        tint = GrayColor,
                    )
                    val tc = item.textCenter
                    val textBoxSize = viewModel.textBoxSize.toDp()
                    Box(
                        modifier = Modifier
                            .size(textBoxSize, textBoxSize)
                            .offset(
                                tc.x.toDp() - textBoxSize / 2, tc.y.toDp() - textBoxSize / 2
                            )
                            //.border(1.dp, Color.Green)
                            .rotate(radToGrad(item.angle)),
                    ) {
                        Text(
                            text = viewModel.items[idx].caption,
                            modifier = Modifier.align(Alignment.CenterEnd), style = TextStyle(
                                //fontSize = 18.sp,
                                fontSize = (viewModel.itemCaptionFontSizeRate * viewModel.iconSide).toSp(),
                                //fontFamily = FontFamily(Font(R.font.if_kica)),
                                fontWeight = FontWeight(400),
                                color = GrayColor,
                                textAlign = TextAlign.Right,
                            )
                        )
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
                            .offset(dx, dy)
                            .background(Color.Black),
                        tint = PrimaryColor,
                    )
                }
                if (true) {
                    val iconSize = size.width.toDp().div(14)
                    val angle = 2f * Math.PI.toFloat() / 12 * 5.3f
                    val xy = (Vec2(cos(angle), sin(angle)) * 0.83f + 1f) * viewModel.radius
                    Icon(
                        painter = painterResource(id = com.surovtsev.common.R.drawable.home_icon),
                        contentDescription = "Localized description",
                        modifier = Modifier
                            .size(iconSize, iconSize)
                            .offset(xy.x.toDp() - iconSize / 2, xy.y.toDp() - iconSize / 2)
                            .background(Color.Black),
                        tint = GrayColor,
                    )
                }

                if (false) {
                    Text(
                        text = radToGradString(prevAngle.floatValue) + " ->" + radToGradString(
                            currAngle.floatValue
                        ) + ": " + radToGradString(
                            commitedDiffAngle.floatValue
                        ) + " " + radToGradString(diffAngle.floatValue),
                        Modifier
                            .align(Alignment.Center)
                            .background(Color.White),
                    )
                    Box(
                        Modifier
                            .offset {
                                IntOffset(
                                    currX.floatValue.roundToInt(), currY.floatValue.roundToInt()
                                )
                            }
                            .background(Color.Blue)
                            .size(50.dp))

                    Box(modifier = Modifier
                        .offset {
                            IntOffset(
                                prevX.floatValue.roundToInt(),
                                prevY.floatValue.roundToInt(),
                            )
                        }
                        .background(Color.Red)
                        .size(50.dp))
                }

                if (false) {
                    Text(
                        text = "$progress",
                        Modifier
                            .align(Alignment.Center)
                            .background(Color.White)
                    )
                }

                Text(
                    text = "Завтраки",
                    style = TextStyle(
                        // fontSize = 32.sp,
                        fontSize = viewModel.iconSide.toSp() * viewModel.orderCaptionFontSizeRate,
                        //fontFamily = FontFamily(Font(R.font.if_kica)),
                        fontWeight = FontWeight(400),
                        color = PrimaryColor,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
