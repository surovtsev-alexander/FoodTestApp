package com.surovtsev.common.ui_elements.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun Progress(
    context: ProgressContext,
    durationMs: Int,
    delayMs: Int,
) {
    var finished by remember { mutableStateOf(false) }
    val progress by animateIntAsState(
        targetValue = if (finished) 100 else 0,
        animationSpec = tween(
            durationMillis = durationMs,
            delayMillis = delayMs,
            easing = LinearEasing,
        ),
        label = "",
    )
    LaunchedEffect(finished) {
        finished = true
    }

    context.updateProgress(progress)
}
