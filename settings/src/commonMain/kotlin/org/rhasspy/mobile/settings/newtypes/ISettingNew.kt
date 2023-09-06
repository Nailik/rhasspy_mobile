package org.rhasspy.mobile.settings.newtypes

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ISettingsDatabase

abstract class ISettingNew<T> : KoinComponent {

    val database = get<ISettingsDatabase>().database

    private val _data by lazy { MutableStateFlow(readValue()) }
    val data by lazy { _data.readOnly }

    var value: T
        get() = data.value
        set(value) {
            _data.value = value
            saveValue(value, false)
        }

    protected abstract fun saveValue(newValue: T, ignoreIfExists: Boolean)

    protected abstract fun readValue(): T

}