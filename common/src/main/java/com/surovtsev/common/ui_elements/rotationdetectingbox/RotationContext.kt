package com.surovtsev.common.ui_elements.rotationdetectingbox

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RotationContext(initialAngle: Float) {
    private val _diffAngle = MutableStateFlow(0f)
    val diffAngle = _diffAngle.asStateFlow()

    private val _commitedDiffAngle = MutableStateFlow(initialAngle)
    val commitedDiffAngle = _commitedDiffAngle.asStateFlow()

    fun updateDiffAngle(value: Float) {
        _diffAngle.value = value
    }

    fun updateCommitedDiffAngle(value: Float) {
        _commitedDiffAngle.value = value
    }
}