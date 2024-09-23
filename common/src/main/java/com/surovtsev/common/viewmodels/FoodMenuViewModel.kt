package com.surovtsev.common.viewmodels

import androidx.lifecycle.ViewModel
import com.surovtsev.common.R
import glm_.vec2.Vec2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class FoodMenuViewModel : ViewModel() {
    data class Item(
        val caption: String,
        val id: Int,
        var center: Vec2 = Vec2(0f, 0f)
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
            field = if (value <= 0) {
                1
            } else {
                value
            }
        }
    var angle = 0f
        set(value) {
            field = value
        }
    var radius = 1f
        set(value) {
            field = value
            iconSide = radius * iconSideRate * 2
            sourcePosition.x = radius * (1f + centerRadiusRate)
            sourcePosition.y = radius
        }
    var iconSide: Float = 0f
        private set

    private val sourcePosition: Vec2 = Vec2(0f, 0f)

    private val iconSideRate = 0.1f
    private val dxRate = 0.1f
    private val centerRadiusRate = 0.8f

    fun updateCoordinates() {
        for (idx in 0 until min(items.count(), 12)) {
            val angle = 2f * Math.PI / 12 * idx + angle

            val center = Vec2(
                radius * (1f + centerRadiusRate * cos(angle).toFloat()),
                radius * (1f + centerRadiusRate * sin(angle).toFloat())
            )
            items[idx].center = center * (progress / 100f) + sourcePosition * (1f - progress / 100f)
        }
    }

}