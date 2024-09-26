package com.surovtsev.screenfoodmenu

import android.view.MenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import com.surovtsev.common.appnavigation.NavigationItem
import com.surovtsev.common.helpers.MathHelper.radToGrad
import com.surovtsev.common.theme.GrayColor
import com.surovtsev.common.ui_elements.generalcontrols.Callback
import com.surovtsev.common.ui_elements.generalcontrols.GeneralControls
import com.surovtsev.common.ui_elements.kolobokscreen.KolobokScreen
import com.surovtsev.common.ui_elements.progress.Progress
import com.surovtsev.common.ui_elements.progress.ProgressContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationDetectingBox
import com.surovtsev.common.viewmodels.DishViewModel
import com.surovtsev.common.viewmodels.FoodMenuViewModel
import kotlin.math.min

@Composable
fun FoodMenuScreen(
    viewModel: FoodMenuViewModel,
    navController: NavController,
) {
    val localDensity = LocalDensity.current

    LaunchedEffect(Unit) {
        viewModel.switchTo(FoodMenuViewModel.State.Welcome)
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
    viewModel: FoodMenuViewModel,
    navController: NavController,
) {
    RotationDetectingBox(
        screenSize = screenSize,
        initialAngle = viewModel.initialAngle,
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
    viewModel: FoodMenuViewModel,
    rotationContext: RotationContext,
    density: Density,
    navController: NavController,
) {

    MenuItems(
        screenSize,
        viewModel,
        rotationContext,
        density,
        navController,
    )

    val homeButtonAction: Callback = {}
    val microphoneButtonAction: Callback = {
        viewModel.switchTo(FoodMenuViewModel.State.Welcome)
    }
    val nextButtonAction: Callback = {
        if (viewModel.state.value == FoodMenuViewModel.State.Welcome) {
            viewModel.switchTo(FoodMenuViewModel.State.Menu)
        } else {
            navController.navigate(NavigationItem.Dish.route)
        }
    }

    GeneralControls(
        screenSize,
        density,
        homeButtonAction,
        microphoneButtonAction,
        nextButtonAction,
    )
}

@Composable
fun BoxScope.MenuItems(
    screenSize: IntSize,
    viewModel: FoodMenuViewModel,
    rotationContext: RotationContext,
    density: Density,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()

    if (state != FoodMenuViewModel.State.Menu) {
        return
    }

    val progressContext = ProgressContext()
    Progress(
        context = progressContext,
        durationMs = 1000,
        delayMs = 0,
    )

    val progress by progressContext.progress.collectAsState()

    viewModel.radius = screenSize.height.toFloat() / 2
    viewModel.angle = rotationContext.initialAngle
    viewModel.progress = progress
    viewModel.updateCoordinates()

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Buttons(
            viewModel = viewModel,
            density = density,
            navController = navController,
        )

        Arrows(
            screenSize = screenSize,
            viewModel = viewModel,
            progressContext = progressContext,
            density = density,
            navController = navController,
        )

        Section(
            density = density,
            viewModel = viewModel,
            progressContext = progressContext,
        )
    }

    DebugControls(progressContext = progressContext)
}

@Composable
fun Buttons(
    viewModel: FoodMenuViewModel,
    density: Density,
    navController: NavController,
) {

    val painterResources = viewModel.items.map {
        painterResource(id = it.id)
    }

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
                    .rotate(radToGrad(item.imageCorrectionAngle + item.angle)),
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
    }
}

@Composable
fun BoxScope.Section(
    density: Density,
    viewModel: FoodMenuViewModel,
    progressContext: ProgressContext,
) {
    val progress by progressContext.progress.collectAsState()

    if (progress < 100) {
        return
    }

    with(density) {
        Text(
            text = "Завтраки",
            style = TextStyle(
                // fontSize = 32.sp,
                fontSize = viewModel.iconSide.toSp() * viewModel.orderCaptionFontSizeRate,
                //fontFamily = FontFamily(Font(R.font.if_kica)),
                fontWeight = FontWeight(400),
                color = com.surovtsev.common.theme.PrimaryColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            ),
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.Center),
        )
    }
}

@Composable
fun Arrows(
    screenSize: IntSize,
    viewModel: FoodMenuViewModel,
    progressContext: ProgressContext,
    density: Density,
    navController: NavController,
) {
    val progress by progressContext.progress.collectAsState()

    if (progress < 100) {
        return
    }

    val painterResources = viewModel.arrows.map {
        painterResource(id = it.id)
    }

    with(density) {
        for (i in 0 until viewModel.arrows.size) {
            val arrow = viewModel.arrows[i]
            val side = screenSize.height.toDp()

            val dim = arrow.dim
            val startPos = arrow.p1
            Image(
                painter = painterResources[i],
                contentDescription = "",
                modifier = Modifier
                    .size(side * dim.x, side * dim.y)
                    .offset(side * startPos.x, side * startPos.y)
            )
        }
    }
}


@Composable
fun BoxScope.DebugControls(
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
