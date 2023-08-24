package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

open class NewBooleanSetting(
    key: NewSettingsEnum,
    initial: Boolean
) : NewISetting<Boolean>(
    key = key,
    initial = initial
) {

    override fun saveValue(newValue: Boolean) {
        database.settingsBooleanValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

    override fun readValue(): Boolean {
        return database.settingsBooleanValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.toBoolean() ?: initial
            }
    }

    private fun Long?.toBoolean(): Boolean? = if (this != null) this == 1L else null
    private fun Boolean.toLong(): Long = if (this) 1L else 0L

}