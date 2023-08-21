package org.rhasspy.mobile.settings.types

import okio.Path
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class PathNullableSetting(
    key: SettingsEnum,
    initial: Path?
) : ISetting<Path?>(
    key = key,
    initial = initial
) {

    override fun readValue(): Path? {
        return database.settingsStringNullableValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.value_?.toPath() ?: initial
            }
    }

    override fun saveValue(newValue: Path?) {
        database.settingsStringNullableValuesQueries.insertOrUpdate(key.name, newValue?.toString())
    }

}