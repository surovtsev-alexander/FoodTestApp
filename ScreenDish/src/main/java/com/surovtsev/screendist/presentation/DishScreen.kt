package com.surovtsev.screendist.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.surovtsev.common.ui_elements.generalcontrols.GeneralControls
import com.surovtsev.common.ui_elements.kolobokscreen.KolobokScreen
import com.surovtsev.common.ui_elements.progress.Progress
import com.surovtsev.common.ui_elements.progress.ProgressContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationContext
import com.surovtsev.common.ui_elements.rotationdetectingbox.RotationDetectingBox
import com.surovtsev.common.viewmodels.DishViewModel
import glm_.vec2.Vec2


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
    val painterResources = viewModel.dishItems.map {
        painterResource(id = it.id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                viewModel.switchTo(DishViewModel.State.Menu)
            },
    ) {
        with(density) {
            val iconSide = viewModel.dishSide.toDp()
            for (idx in 0 until viewModel.dishItems.size) {
                val item = viewModel.dishItems[idx]
                val c = item.center
                Image(
                    painter = painterResources[idx],
                    contentDescription = "",
                    modifier = Modifier
                        .size(iconSide, iconSide)
                        .offset(c.x.toDp() - iconSide / 2, c.y.toDp() - iconSide / 2)
                )
            }

            DishInfo(
                viewModel = viewModel,
                density = density,
            )
        }
    }
}

@Composable
fun BoxScope.DishInfo(
    viewModel: DishViewModel,
    density: Density,
) {
    with(density) {
        val iconSide = viewModel.dishSide.toDp()
        val radius = viewModel.radius.toDp()
        val w = iconSide * 0.95f
        val h = iconSide * viewModel.dishTextHeightRate * 0.85f
        Box(
            modifier = Modifier
                .size(w, h)
                .offset(radius - w / 2, radius - h / 2)
        ) {
            data class Box(
                val p1: Vec2,
                val p2: Vec2,
            ) {
                fun width() = w * (p2.x - p1.x)
                fun height() = h * (p2.y - p1.y)
            }

            val y1 = 0.15f
            val y2 = 1f - y1
            val x1 = y1 * (h / w)
            val x2 = 1f - x1

            val boxes = listOf(
                Box(Vec2(0f, y1), Vec2(x1, y2)),
                Box(Vec2(x1, 0f), Vec2(x2, 1f)),
                Box(Vec2(x2, y1), Vec2(1f, y2)),
            )

            boxes.forEach { box ->
                Box(
                    modifier = Modifier
                        .size(box.width(), box.height())
                        .offset(w * box.p1.x, h * box.p1.y)
                        .background(color = Color(0xBF1F1F1F))
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .weight(0.6f),
                )
                {
                    Text(
                        text = "Фирменная шакшука \nс сезонной зеленью",
                        style = TextStyle(
                            fontSize = 28.sp,
                            //fontFamily = FontFamily(Font(R.font.if_kica)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFAA926),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.3f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.1f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.8f),
                    ) {
                        val labels = listOf(
                            "Белки — 16",
                            "Жиры — 36",
                            "Углеводы — 20",
                            "Калории — 332",
                        )
                        for (i in 0 until labels.size) {
                            val label = labels[i]
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontSize = radius.toSp() / 40,
                                    //fontFamily = FontFamily(Font(R.font.tilda_sans)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFFFFFFFF),
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically),
                            )
                            if (i < (labels.size - 1)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.1f)
                    )
                }
            }
        }
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
