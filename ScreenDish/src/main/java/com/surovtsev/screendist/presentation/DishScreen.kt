package com.surovtsev.screendist.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import com.surovtsev.common.theme.GrayColor
import com.surovtsev.common.ui_elements.generalcontrols.GeneralControls
import com.surovtsev.common.ui_elements.kolobokscreen.KolobokScreen
import com.surovtsev.common.ui_elements.progress.Progress
import com.surovtsev.common.ui_elements.progress.ProgressContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationDetectingBox
import com.surovtsev.common.viewmodels.DishViewModel


@Composable
fun DishScreen(
    viewModel: DishViewModel,
    navController: NavController,
) {
    val localDensity = LocalDensity.current

    KolobokScreen { screenSize ->
        ScreenContent(
            screenSize = screenSize,
            density = localDensity,
            viewModel = viewModel,
            navController = navController,
        )
    }
}

@Composable
fun ScreenContent(
    screenSize: IntSize,
    density: Density,
    viewModel: DishViewModel,
    navController: NavController,
) {
    RotationDetectingBox(
        screenSize = screenSize,
        initialAngle = 0f,
    ) { rotationContext ->
        Controls(
            screenSize = screenSize,
            viewModel = viewModel,
            rotationContext = rotationContext,
            density = density,
            navController = navController,
        )
    }
}

@Composable
fun BoxScope.Controls(
    screenSize: IntSize,
    viewModel: DishViewModel,
    rotationContext: RotationContext,
    density: Density,
    navController: NavController,
) {
    val progressContext = ProgressContext()
    Progress(
        context = progressContext,
        durationMs = 1000,
        delayMs = 500,
    )

    val progress by progressContext.progress.collectAsState()

    viewModel.radius = screenSize.height.toFloat() / 2
    viewModel.progress = progress
    viewModel.updateCoordinates()

    Buttons(
        viewModel = viewModel,
        density = density,
        navController = navController,
    )

    GeneralControls(screenSize, density)
}

@Composable
fun Buttons(
    viewModel: DishViewModel,
    density: Density,
    navController: NavController,
) {
    val ctx = LocalContext.current

    val painterResources = viewModel.items.map {
        painterResource(id = it.id)
    }

    with(density) {
        val iconSide = viewModel.iconSide.toDp()
        for (idx in 0 until viewModel.itemsCountToDisplay) {
            val item = viewModel.items[idx]
            val c = item.center
            Image(
                painter = painterResources[idx],
                contentDescription = "",
                modifier = Modifier
                    .size(iconSide, iconSide)
                    .offset(c.x.toDp() - iconSide / 2, c.y.toDp() - iconSide / 2)
            )
        }
        val item = viewModel.centerItem
        val c = item.center
        Image(
            painter = painterResource(id = item.id),
            contentDescription = "",
            modifier = Modifier
                .size(iconSide, iconSide)
                .offset(c.x.toDp() - iconSide / 2, c.y.toDp() - iconSide / 2)
        )

    }
}
