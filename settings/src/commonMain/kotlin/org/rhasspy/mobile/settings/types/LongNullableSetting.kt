package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class LongNullableSetting(
    private val key: SettingsEnum,
    initial: Long?
) : ISetting<Long?>() {

    override val data = database.database.settingsLongNullableValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { if (it != null) it.value_ else initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: Long?) {
        database.database.settingsLongNullableValuesQueries.insertOrUpdate(key.name, newValue)
    }

}