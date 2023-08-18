package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordCustomListSetting(
    private val key: SettingsEnum,
    initial: ImmutableList<PorcupineCustomKeyword>
) : ISetting<ImmutableList<PorcupineCustomKeyword>>() {

    override val data = database.database.settingsPorcupineKeywordListValuesQueries
        .select(key.name)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { list ->
            list.map {
                PorcupineCustomKeyword(
                    fileName = it.value_,
                    isEnabled = it.enabled == 1L,
                    sensitivity = it.sensitivity.toFloat()
                )
            }.toTypedArray().toImmutableList()
        }
        .simpleStateIn(initial)

    override fun saveValue(newValue: ImmutableList<PorcupineCustomKeyword>) {
        database.database.settingsPorcupineKeywordListValuesQueries.transaction {
            newValue.forEach {
                database.database.settingsPorcupineKeywordListValuesQueries
                    .insertOrUpdate(
                        id = key.name,
                        value_ = it.fileName,
                        enabled = if (it.isEnabled) 1 else 0,
                        sensitivity = it.sensitivity.toLong()
                    )
            }
        }
    }

}