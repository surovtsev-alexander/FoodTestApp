
package com.surovtsev.common.ui_elements.progress

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressContext {
    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    fun updateProgress(value: Int) {
        _progress.value = value
    }
}
