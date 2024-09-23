package com.surovtsev.common.viewmodels

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.surovtsev.common.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class FoodMenuViewModel : ViewModel() {
    data class Position(
        var x: Dp = 0.dp,
        var y: Dp = 0.dp
    )

    data class Item(
        val caption: String,
        val id: Int,
        val center: Position = Position(),
    )

    val items = listOf(
        Item("Напитки", R.drawable.drinks_icon),
        Item("Кофе", R.drawable.coffee_icon),
        Item("Салаты", R.drawable.salats_icon),
        Item("Закуски", R.drawable.snacks_icon),
        Item("Бургеры", R.drawable.burger_icon),
        Item("Горячее", R.drawable.main_dishes_icon),
        Item("Десерты", R.drawable.deserts_icon),
        Item("Пицца", R.drawable.pizza_icon),
    )

    var progress = 0
        set(value) {
            field = value
        }
    var angle = 0f
        set(value) {
            field = value
        }
    var radius = 1.dp
        set(value) {
            field = value
            iconSide = radius * iconSideRate * 2
        }
    var iconSide: Dp = 0.dp
        private set

    private val iconSideRate = 0.1f
    private val dxRate = 0.1f
    private val centerRadiusRate = 0.8f

    fun UpdateCoordinates() {
        for (idx in 0 until min(items.count(), 12)) {
            val angle = 2f * Math.PI / 12 * idx + angle

            val center = items[idx].center;
            center.x = radius * (1f + centerRadiusRate * cos(angle).toFloat())
            center.y = radius * (1f + centerRadiusRate * sin(angle).toFloat())
        }
    }

}