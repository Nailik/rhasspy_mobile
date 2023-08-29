package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class FloatSetting(
    key: SettingsEnum,
    initial: Float
) : ISetting<Float>(
    key = key,
    initial = initial
) {

    override fun readValue(): Float {
        return database.settingsLongValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.toFloat() ?: initial
            }
    }

    override fun saveValue(newValue: Float) {
        database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}