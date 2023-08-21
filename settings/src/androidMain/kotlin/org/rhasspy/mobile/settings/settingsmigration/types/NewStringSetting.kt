package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewStringSetting(
    key: NewSettingsEnum,
    initial: String
) : NewISetting<String>(
    key = key,
    initial = initial
) {

    override fun readValue(): String {
        return database.settingsStringValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it ?: initial
            }
    }

    override fun saveValue(newValue: String) {
        database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue)
    }

}