package com.surovtsev.screendist.presentation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
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

    with(density) {

    }
}
