package com.surovtsev.common.ui_elements.kolobokscreenframe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import com.surovtsev.common.theme.PrimaryColor


@Composable
fun KolobokScreenFrame(
    screenSize: IntSize,
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
}
