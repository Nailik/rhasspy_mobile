package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class FloatSetting(
    private val key: SettingsEnum,
    initial: Float
) : ISetting<Float>() {

    override val data = database.database.settingsLongValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it?.toFloat() ?: initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: Float) {
        database.database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}