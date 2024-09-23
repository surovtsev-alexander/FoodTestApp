package com.surovtsev.common.viewmodels

import androidx.lifecycle.ViewModel
import com.surovtsev.common.R

class FoodMenuViewModel: ViewModel() {
    val items = listOf(
        R.drawable.drinks_icon,
        R.drawable.coffee_icon,
        R.drawable.salats_icon,
        R.drawable.snacks_icon,
        R.drawable.burger_icon,
        R.drawable.main_dishes_icon,
        R.drawable.deserts_icon,
        R.drawable.pizza_icon,
    )
}