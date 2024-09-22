package com.surovtsev.common.appnavigation

enum class Screen {
    FoodMenu,
    Dish,
}

sealed class NavigationItem(val route: String) {
    object FoodMenu : NavigationItem(Screen.FoodMenu.name)
    object Dish : NavigationItem(Screen.Dish.name)
}
