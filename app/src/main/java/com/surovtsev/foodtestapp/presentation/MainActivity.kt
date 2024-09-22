package com.surovtsev.foodtestapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.surovtsev.common.appnavigation.NavigationItem
import com.surovtsev.common.viewmodels.DishViewModel
import com.surovtsev.common.viewmodels.FoodMenuViewModel
import com.surovtsev.common.theme.FoodTestAppTheme
import com.surovtsev.screendist.presentation.DishScreen
import com.surovtsev.screenfoodmenu.FoodMenuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            FoodTestAppTheme {
                NavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFF000000)),
                    navController = navController,
                    startDestination = NavigationItem.FoodMenu.route,
                ) {
                    composable(NavigationItem.FoodMenu.route) {
                        val viewModel: FoodMenuViewModel by viewModels()
                        FoodMenuScreen(viewModel, navController)
                    }
                    composable(NavigationItem.Dish.route) {
                        val viewModel: DishViewModel by viewModels()
                        DishScreen(viewModel, navController)
                    }
                }
            }
        }
    }
}
