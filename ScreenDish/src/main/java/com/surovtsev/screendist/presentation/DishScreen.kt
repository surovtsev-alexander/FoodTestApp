package com.surovtsev.screendist.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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

    LaunchedEffect(Unit) {
        viewModel.switchTo(DishViewModel.State.Menu)
    }

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

    val state by viewModel.state.collectAsState()

    if (state == DishViewModel.State.Menu) {
        MenuButtons(
            viewModel = viewModel,
            density = density,
        )
    } else {
        Dish(
            viewModel = viewModel,
            density = density
        )
    }

    GeneralControls(screenSize, density)
}

@Composable
fun Dish(
    viewModel: DishViewModel,
    density: Density,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                viewModel.switchTo(DishViewModel.State.Menu)
            },
    ) {
    }
}

@Composable
fun MenuButtons(
    viewModel: DishViewModel,
    density: Density,
) {
    val painterResources = viewModel.items.map {
        painterResource(id = it.id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                viewModel.switchTo(DishViewModel.State.Dish)
            },
    ) {
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
}
