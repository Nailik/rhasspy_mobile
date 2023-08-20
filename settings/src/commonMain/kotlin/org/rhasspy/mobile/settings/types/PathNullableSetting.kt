package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import okio.Path
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class PathNullableSetting(
    private val key: SettingsEnum,
    private val initial: Path?
) : ISetting<Path?>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): Path? {
        return database.database.settingsStringNullableValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.value_?.toPath() ?: initial
            }
    }

    override fun saveValue(newValue: Path?) {
        data.value = newValue
        database.database.settingsStringNullableValuesQueries.insertOrUpdate(key.name, newValue?.toString())
    }

}