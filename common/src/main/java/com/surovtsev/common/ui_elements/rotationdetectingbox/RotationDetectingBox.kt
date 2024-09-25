package com.surovtsev.common.ui_elements.rotationdetectingbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.surovtsev.common.helpers.MathHelper.calculateAngle
import com.surovtsev.common.helpers.MathHelper.normalRad
import com.surovtsev.common.helpers.MathHelper.radToGradString
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset

@Composable
fun RotationDetectingBox(
    initialAngle: Float,
    screenSize: IntSize,
    content: @Composable BoxScope.(rotationContext: RotationContext) -> Unit,
) {
    val rotationContext = RotationContext(initialAngle = initialAngle)

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

        content(rotationContext)

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
