package com.surovtsev.screendist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.surovtsev.common.viewmodels.DishViewModel


@Composable
fun DishScreen(
    dishViewModel: DishViewModel,
    navController: NavController,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxSize(),
    ) {
        Text(text = "DishMenuScreen")
    }
}