package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class StringSetting(
    private val key: SettingsEnum,
    initial: String
) : ISetting<String>() {

    override val data = database.database.settingsStringValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it ?: initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: String) {
        database.database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue)
    }

}