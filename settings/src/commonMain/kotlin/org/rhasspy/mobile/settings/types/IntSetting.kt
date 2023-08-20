package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class IntSetting(
    private val key: SettingsEnum,
    private val initial: Int
) : ISetting<Int>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): Int {
        return database.database.settingsLongValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.toInt() ?: initial
            }
    }

    override fun saveValue(newValue: Int) {
        data.value = newValue
        database.database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}