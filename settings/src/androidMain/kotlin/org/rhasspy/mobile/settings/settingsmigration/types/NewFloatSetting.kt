package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewFloatSetting(
    key: NewSettingsEnum,
    initial: Float
) : NewISetting<Float>(
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