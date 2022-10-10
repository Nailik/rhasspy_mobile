package org.rhasspy.mobile.settings

import kotlinx.coroutines.flow.StateFlow

open class Element<T>(private val setting: Setting<T>) {
    val flow: StateFlow<T> get() = setting.data
    open fun set(value: T) {
        setting.data.value = value
    }
}