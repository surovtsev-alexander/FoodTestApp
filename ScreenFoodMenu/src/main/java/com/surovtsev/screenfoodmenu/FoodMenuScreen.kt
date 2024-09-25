package com.surovtsev.screenfoodmenu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.surovtsev.common.helpers.MathHelper.calculateAngle
import com.surovtsev.common.helpers.MathHelper.normalRad
import com.surovtsev.common.helpers.MathHelper.radToGrad
import com.surovtsev.common.helpers.MathHelper.radToGradString
import com.surovtsev.common.theme.GrayColor
import com.surovtsev.common.theme.PrimaryColor
import com.surovtsev.common.ui_elements.generalcontrols.GeneralControls
import com.surovtsev.common.ui_elements.kolobokscreenframe.KolobokScreenFrame
import com.surovtsev.common.ui_elements.progress.Progress
import com.surovtsev.common.ui_elements.progress.ProgressContext
import com.surovtsev.common.viewmodels.FoodMenuViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun FoodMenuScreen(
    viewModel: FoodMenuViewModel,
    navController: NavController,
) {
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val localDensity = LocalDensity.current

    Box(
        modifier = Modifier
            .padding(1.35.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .background(color = Color.White)
            .onGloballyPositioned { coordinates -> screenSize = coordinates.size },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            KolobokScreenFrame(screenSize) { screenSize ->
                ScreenContent(
                    screenSize = screenSize,
                    density = localDensity,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun ScreenContent(
    screenSize: IntSize,
    density: Density,
    viewModel: FoodMenuViewModel,
) {
    val rotationContext = RotationContext(
        -2 * Math.PI.toFloat() * (viewModel.items.count() - 1) / 12 / 2
    )

    Controls(
        screenSize = screenSize,
        rotationContext = rotationContext,
    ) {
        Content(
            screenSize = screenSize,
            viewModel = viewModel,
            rotationContext = rotationContext,
            density = density,
        )

        GeneralControls(screenSize, density)
    }
}

class RotationContext(initialAngle: Float) {
    private val _diffAngle = MutableStateFlow(0f)
    val diffAngle = _diffAngle.asStateFlow()

    private val _commitedDiffAngle = MutableStateFlow(initialAngle)
    val commitedDiffAngle = _commitedDiffAngle.asStateFlow()

    fun updateDiffAngle(value: Float) {
        _diffAngle.value = value
    }

    fun updateCommitedDiffAngle(value: Float) {
        _commitedDiffAngle.value = value
    }
}

@Composable
fun Controls(
    screenSize: IntSize,
    rotationContext: RotationContext,
    content: @Composable BoxScope.() -> Unit,
) {
    val currX = remember { mutableFloatStateOf(screenSize.width.toFloat()) }
    val currY = remember { mutableFloatStateOf(screenSize.height.toFloat() / 2) }
    val prevX = remember { mutableFloatStateOf(0f) }
    val prevY = remember { mutableFloatStateOf(9f) }
    val prevAngle = remember { mutableFloatStateOf(0f) }
    val currAngle = remember { mutableFloatStateOf(0f) }

    val diffAngle by rotationContext.diffAngle.collectAsState()
    val commitedDiffAngle by rotationContext.commitedDiffAngle.collectAsState()

    val radius = screenSize.width.toFloat() / 2

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
                        radius,
                    )
                }, onDrag = { _: PointerInputChange, dragAmount: Offset ->
                    currX.floatValue = (currX.floatValue + dragAmount.x).coerceIn(
                        0f, screenSize.width.toFloat() - 50.dp.toPx()
                    )
                    currY.floatValue = (currY.floatValue + dragAmount.y).coerceIn(
                        0f, screenSize.height.toFloat() - 50.dp.toPx()
                    )
                    currAngle.floatValue = calculateAngle(
                        currAngle.floatValue,
                        currX.floatValue,
                        currY.floatValue,
                        radius,
                    )
                    rotationContext.updateDiffAngle(
                        currAngle.floatValue - prevAngle.floatValue
                    )

                }, onDragCancel = {

                }, onDragEnd = {
                    rotationContext.updateCommitedDiffAngle(
                        normalRad(commitedDiffAngle + diffAngle)
                    )
                    rotationContext.updateDiffAngle(0f)
                })
            },
    ) {

        content()

        DebugControls(
            prevAngle = prevAngle,
            currAngle = currAngle,
            rotationContext = rotationContext,
            currX = currX,
            currY = currY,
            prevX = prevX,
            prevY = prevY,
        )
    }
}

@Composable
fun BoxScope.Content(
    screenSize: IntSize,
    viewModel: FoodMenuViewModel,
    rotationContext: RotationContext,
    density: Density,
) {
    val progressContext = ProgressContext()
    Progress(
        context = progressContext,
        durationMs = 1000,
        delayMs = 500,
    )

    val ctx = LocalContext.current

    val progress by progressContext.progress.collectAsState()

    val painterResources = viewModel.items.map {
        painterResource(id = it.id)
    }

    val commitedDiffAngle by rotationContext.commitedDiffAngle.collectAsState()
    val diffAngle by rotationContext.diffAngle.collectAsState()

    viewModel.radius = screenSize.height.toFloat() / 2
    viewModel.angle = commitedDiffAngle + diffAngle
    viewModel.progress = progress

    viewModel.updateCoordinates()

    with(density) {
        val iconSide = viewModel.iconSide.toDp()
        for (idx in 0 until min(12, viewModel.items.count())) {
            val item = viewModel.items[idx]
            val c = item.center
            Icon(
                painter = painterResources[idx],
                contentDescription = "",
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
                    .rotate(radToGrad(item.angle)),
            ) {
                Text(
                    text = viewModel.items[idx].caption,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    style = TextStyle(
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

        DebugXX(progressContext = progressContext)
    }
}

@Composable
fun BoxScope.DebugXX(
    progressContext: ProgressContext,
) {
    if (false) {
        val progress = progressContext.progress.collectAsState()
        Text(
            text = "${progress.value}",
            Modifier
                .align(Alignment.Center)
                .background(Color.White)
        )
    }
}

@Composable
fun BoxScope.DebugControls(
    prevAngle: FloatState,
    currAngle: FloatState,
    rotationContext: RotationContext,
    currX: FloatState,
    currY: FloatState,
    prevX: FloatState,
    prevY: FloatState,
) {
    if (false) {
        val commitedDiffAngle by rotationContext.commitedDiffAngle.collectAsState()
        val diffAngle by rotationContext.diffAngle.collectAsState()

        Text(
            text = radToGradString(prevAngle.floatValue) + " ->" + radToGradString(
                currAngle.floatValue
            ) + ": " + radToGradString(
                commitedDiffAngle
            ) + " " + radToGradString(diffAngle),
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
}

