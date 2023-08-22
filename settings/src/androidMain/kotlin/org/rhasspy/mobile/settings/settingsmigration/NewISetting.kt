package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.platformspecific.readOnly

abstract class NewISetting<T>(
    val key: NewSettingsEnum,
    val initial: T
) : KoinComponent {

    val database = MigrateToDatabase.database

    init {
        database.transaction {
            database.settingsIdsQueries.insertOrIgnore(key.name)
            val numberOfRowsAffected = database.settingsIdsQueries.selectChanges().executeAsOne()
            if (numberOfRowsAffected > 0) {
                saveValue(initial)
            }
        }
    }

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