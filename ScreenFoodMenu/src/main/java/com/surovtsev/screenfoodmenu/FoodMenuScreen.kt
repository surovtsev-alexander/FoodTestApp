package com.surovtsev.screenfoodmenu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.common.theme.PrimaryColor
import com.surovtsev.common.viewmodels.FoodMenuViewModel

@Composable
fun FoodMenuScreen(
    viewModel: FoodMenuViewModel,
    navController: NavController,
) {
    Box(
        modifier = Modifier
            .padding(1.35.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .background(color = Color(0xFF000000)),
        contentAlignment = Alignment.Center,
    ) {
        Circle()
    }
}

@Composable
fun Circle(
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned { coordinates -> size = coordinates.size },
        onDraw = {
            val stroke = 20f
            val radius = (size.width.toFloat() - stroke) / 2

            drawCircle(
                color = PrimaryColor,
                radius = radius,
                style = Stroke(
                    width = stroke,
                )
            )
        }
    )
}