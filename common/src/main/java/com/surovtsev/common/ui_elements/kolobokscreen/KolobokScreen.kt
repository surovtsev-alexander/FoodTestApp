package com.surovtsev.common.ui_elements.kolobokscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.surovtsev.common.theme.PrimaryColor


@Composable
fun KolobokScreen(
    screenContent: @Composable BoxScope.(screenSize: IntSize,) -> Unit,
) {
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .padding(1.35.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .background(color = Color.White)
            .onGloballyPositioned { coordinates -> screenSize = coordinates.size },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = com.surovtsev.common.R.drawable.background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Icon(
            painter = painterResource(id = com.surovtsev.common.R.drawable.back),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "",
            tint = PrimaryColor,
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circlePath = Path().apply {
                addOval(Rect(center, screenSize.width.toFloat() / 2))
            }
            clipPath(circlePath, clipOp = ClipOp.Difference) {
                drawRect(SolidColor(Color.Black))
            }
        }

        screenContent(screenSize)
    }
}
