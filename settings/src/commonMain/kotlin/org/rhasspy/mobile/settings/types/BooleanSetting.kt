package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class BooleanSetting(
    private val key: SettingsEnum,
    private val initial: Boolean
) : ISetting<Boolean>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): Boolean {
        return database.database.settingsBooleanValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                if (it != null) it == 1L else initial
            }
    }

    override fun saveValue(newValue: Boolean) {
        data.value = newValue
        database.database.settingsBooleanValuesQueries.insertOrUpdate(key.name, if (newValue) 1L else 0L)
    }

}