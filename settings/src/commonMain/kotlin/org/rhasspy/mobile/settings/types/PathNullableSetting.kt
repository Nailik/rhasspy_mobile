package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import okio.Path
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class PathNullableSetting(
    private val key: SettingsEnum,
    initial: Path?
) : ISetting<Path?>() {

    override val data = database.database.settingsStringNullableValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it?.value_?.toPath() }
        .simpleStateIn(initial)

    override fun saveValue(newValue: Path?) {
        database.database.settingsStringNullableValuesQueries.insertOrUpdate(key.name, newValue?.toString())
    }

}