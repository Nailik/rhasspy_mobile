package org.rhasspy.mobile.settings

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class ISetting<T> : KoinComponent {
//TODO -> when database is completely new created make a savevalue on all initial values
    val database = get<DatabaseConnection>()

    abstract val data: StateFlow<T>

    var value: T
        get() = data.value
        set(value) = saveValue(value)

    abstract fun saveValue(newValue: T)

}