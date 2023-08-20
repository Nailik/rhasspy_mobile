package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class FloatSetting(
    private val key: SettingsEnum,
    private val initial: Float
) : ISetting<Float>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): Float {
        return database.database.settingsLongValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.toFloat() ?: initial
            }
    }

    override fun saveValue(newValue: Float) {
        data.value = newValue
        database.database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}