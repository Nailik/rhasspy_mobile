package org.rhasspy.mobile.settings

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.readOnly

abstract class ISettingObject<T> : KoinComponent {

    val database = get<ISettingsDatabase>().database

    private val _data by lazy { MutableStateFlow(readValue()) }
    val data by lazy { _data.readOnly }

    var value: T
        get() = data.value
        set(value) {
            _data.value = value
            saveValue(value)
        }

    protected abstract fun saveValue(newValue: T)
    protected abstract fun readValue(): T

}