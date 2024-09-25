package com.surovtsev.common.viewmodels

import androidx.lifecycle.ViewModel
import com.surovtsev.common.R
import glm_.vec2.Vec2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DishViewModel() : ViewModel() {
    enum class State {
        Menu,
        Dish,
    }

    private val _state = MutableStateFlow<State>(State.Menu)
    val state = _state.asStateFlow()

    fun switchTo(state: State) {
        _state.value = state
    }

    data class Item(
        val caption: String,
        val id: Int,
        val section: Int,
        var angle: Float = 0f,
        var center: Vec2 = Vec2(0f, 0f),
    )

    val items = listOf(
        Item("", R.drawable.hummuspng, 8),
        Item("", R.drawable.porridge, 9),
        Item("", R.drawable.waffles, 10),
        Item("", R.drawable.benedict, 2),
        Item("", R.drawable.salat, 3),
        Item("", R.drawable.puncake, 4),
    )

    val centerItem = Item("", R.drawable.shakshuka, 0)

    val sectors = 12
    val itemsCountToDisplay = min(items.count(), 10)

    var progress = 0
        set(value) {
            field = value
        }
    var radius = 1f
        set(value) {
            field = value
            iconSide = radius * iconSideRate
            sourcePosition.x = radius * (1f + centerRadiusRate)
            sourcePosition.y = radius
        }
    var iconSide: Float = 0f
        private set

    private val sourcePosition: Vec2 = Vec2(0f, 0f)

    private val iconSideRate = 0.2f
    private val centerRadiusRate = 0.8f

    fun updateCoordinates() {
        val halfItems = (itemsCountToDisplay + 1) / 2

        for (idx in 0 until itemsCountToDisplay) {
            val item = items[idx]

            item.angle = (2f * Math.PI.toFloat() / sectors * item.section)// * (progress / 100f)

            val xy = Vec2(cos(item.angle), sin(item.angle))
            item.center = (xy * centerRadiusRate + 1f) * radius
            item.center = item.center * (progress / 100f) + sourcePosition * (1f - progress / 100f)
        }

        centerItem.center = Vec2(radius)
        centerItem.center = centerItem.center * (progress / 100f) + sourcePosition * (1f - progress / 100f)
    }
}
