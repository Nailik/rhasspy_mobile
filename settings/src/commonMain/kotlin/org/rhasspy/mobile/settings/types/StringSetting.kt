package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class StringSetting(
    private val key: SettingsEnum,
    private val initial: String
) : ISetting<String>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): String {
        return database.database.settingsStringValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it ?: initial
            }
    }

    override fun saveValue(newValue: String) {
        data.value = newValue
        database.database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue)
    }

}