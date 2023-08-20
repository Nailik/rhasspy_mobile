package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class LongNullableSetting(
    private val key: SettingsEnum,
    private val initial: Long?
) : ISetting<Long?>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): Long? {
        return database.database.settingsLongNullableValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                if (it != null) it.value_ else initial
            }
    }

    override fun saveValue(newValue: Long?) {
        data.value = newValue
        database.database.settingsLongNullableValuesQueries.insertOrUpdate(key.name, newValue)
    }

}