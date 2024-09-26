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
        var imageInitialSector: Int,
        var angle: Float = 0f,
        var center: Vec2 = Vec2(0f, 0f),
        var textCenter: Vec2 = Vec2(0f, 0f),
    ) {
        val imageCorrectionAngle = Math.PI.toFloat() * 2 * (12 - imageInitialSector) / 12
    }

    val items = listOf(
        Item("Напитки", R.drawable.drinks_icon, 8),
        Item("Кофе", R.drawable.coffee_icon, 9),
        Item("Салаты", R.drawable.salats_icon, 10),
        Item("Закуски", R.drawable.snacks_icon, 11),
        Item("Бургеры", R.drawable.burger_icon, 1),
        Item("Горячее", R.drawable.main_dishes_icon, 2),
        Item("Десерты", R.drawable.deserts_icon, 3),
        Item("Пицца", R.drawable.pizza_icon, 4),
    )

    val itemsCountToDisplay = min(items.count(), 12)
    val gapInTheMiddle = true

    val initialAngle = -2 * Math.PI.toFloat() * (items.count() - 1 + if (gapInTheMiddle) 1 else 0) / 2 / 12

    var progress = 0
        set(value) {
            field = value
        }
    var angle = 0f
        set(value) {
            field = value
        }
    var radius = 1f
        set(value) {
            field = value
            iconSide = radius * iconSideRate
            textBoxSize = radius * textSizeRate
            sourcePosition.x = radius * (1f + centerRadiusRate)
            sourcePosition.y = radius
        }
    var iconSide: Float = 0f
        private set

    private val sourcePosition: Vec2 = Vec2(0f, 0f)

    private val iconSideRate = 0.2f
    private val dxRate = 0.1f
    private val centerRadiusRate = 0.8f
    private val textCenterRate = 0.5f
    private val textSizeRate = 0.3f
    val itemCaptionFontSizeRate = 0.3f
    val orderCaptionFontSizeRate = 0.6f
    var textBoxSize: Float = 0f
        private set

    class ArrowInfo(
        val id: Int,
        x1: Int, x2: Int, x3: Int,
        y1: Int, y2: Int, y3: Int,
    ) {
        val p1: Vec2
        val p2: Vec2
        val dim: Vec2
        val center: Vec2

        init {
            val width = x1 + x2 + x3
            val height = y1 + y2 + y3

            p1 = Vec2(1f * x1 / width, 1f * y1 / height)
            dim = Vec2(1f * x2 / width, 1f * y2 / height)
            p2 = p1 + dim

            center = p1 + dim / 2
        }
    }

    val arrows = listOf(
        ArrowInfo(
            R.drawable.arrow_up,
            620, 87, 373,
            399, 126, 555
        ),
        ArrowInfo(
            R.drawable.arrow_down,
            620, 87, 373,
            557, 125, 397
        )
    )

    fun updateCoordinates() {
        val halfItems = (itemsCountToDisplay + 1) / 2

        for (idx in 0 until itemsCountToDisplay) {
            val item = items[idx]

            val pos = if (gapInTheMiddle && idx >= halfItems) idx + 1 else idx
            item.angle = (2f * Math.PI.toFloat() / 12 * pos + angle) * (progress / 100f)

            val xy = Vec2(cos(item.angle), sin(item.angle))
            //items[idx].center = center * (progress / 100f) + sourcePosition * (1f - progress / 100f)
            item.center = (xy * centerRadiusRate + 1f) * radius
            item.textCenter = (xy * textCenterRate + 1f) * radius
        }
    }

}