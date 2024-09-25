package com.surovtsev.common.helpers

import kotlin.math.acos
import kotlin.math.sqrt

object MathHelper {
    fun calculateAngle(prevValue: Float, x: Float, y: Float, radius: Float): Float {
        val dx = x - radius
        val dy = y - radius
        val l = sqrt(dx * dx + dy * dy)
        if (l < radius / 10) {
            return prevValue
        }
        val nX = dx / l
        val aC = acos(nX)
        return if (dy > 0) {
            aC
        } else {
            2 * Math.PI.toFloat() - aC
        }
    }

    fun radToGrad(a: Float) = a / Math.PI.toFloat() * 180
    fun normalGrad(a: Float) = (a + 540) % 360 - 180
    fun radToGradString(a: Float) = "%.2f".format(normalGrad(radToGrad(a)))
    fun normalRad(a: Float): Float {
        var res = a
        val doublePiFloat = Math.PI.toFloat() * 2
        while (res < -1 * doublePiFloat) {
            res += doublePiFloat
        }
        while (res > doublePiFloat) {
            res -= doublePiFloat
        }
        return res
    }
}
