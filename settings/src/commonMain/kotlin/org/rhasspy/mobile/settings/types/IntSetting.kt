package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class IntSetting(
    private val key: SettingsEnum,
    initial: Int
) : ISetting<Int>() {

    override val data = database.database.settingsLongValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it?.toInt() ?: initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: Int) {
        database.database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}