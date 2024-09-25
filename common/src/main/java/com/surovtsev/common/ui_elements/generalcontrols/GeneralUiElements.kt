package com.surovtsev.common.ui_elements.generalcontrols

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.surovtsev.common.theme.GrayColor
import com.surovtsev.common.theme.PrimaryColor
import glm_.vec2.Vec2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GeneralControls(
    screenSize: IntSize,
    density: Density,
) {

    val widthDp = with(density) {
        screenSize.width.toDp()
    }

    run {
        val iconSize = widthDp.div(8)
        val dx = widthDp / 30
        val dy = (widthDp - iconSize).div(2)
        Icon(
            painter = painterResource(id = com.surovtsev.common.R.drawable.microphone),
            contentDescription = "",
            modifier = Modifier
                .size(iconSize, iconSize)
                .offset(dx, dy)
                .background(Color.Black),
            tint = PrimaryColor,
        )
    }

    with(density) {
        val iconSize = widthDp.div(14)
        val angle = 2f * Math.PI.toFloat() / 12 * 5.3f
        val xy = (Vec2(cos(angle), sin(angle)) * 0.83f + 1f) * screenSize.width / 2
        Icon(
            painter = painterResource(id = com.surovtsev.common.R.drawable.home_icon),
            contentDescription = "",
            modifier = Modifier
                .size(iconSize, iconSize)
                .offset(xy.x.toDp() - iconSize / 2, xy.y.toDp() - iconSize / 2)
                .background(Color.Black),
            tint = GrayColor,
        )
    }

    run {
        val iconSize = widthDp.div(8)
        val dx = widthDp - widthDp / 30 - iconSize
        val dy = (widthDp - iconSize).div(2)
        Icon(
            painter = painterResource(id = com.surovtsev.common.R.drawable.breakfast_icon),
            contentDescription = "",
            modifier = Modifier
                .size(iconSize, iconSize)
                .offset(dx, dy)
                .background(Color.Black),
            tint = PrimaryColor,
        )

    }
}