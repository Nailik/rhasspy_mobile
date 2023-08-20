package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordCustomListSetting(
    private val key: SettingsEnum,
    private val initial: ImmutableList<PorcupineCustomKeyword> //TODO initial
) : ISetting<ImmutableList<PorcupineCustomKeyword>>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): ImmutableList<PorcupineCustomKeyword> {
        return database.database.settingsPorcupineKeywordListValuesQueries
            .select(key.name)
            .executeAsList().map {
                PorcupineCustomKeyword(
                    fileName = it.value_,
                    isEnabled = it.enabled == 1L,
                    sensitivity = it.sensitivity.toFloat()
                )
            }.toTypedArray().toImmutableList()
    }

    override fun saveValue(newValue: ImmutableList<PorcupineCustomKeyword>) {
        data.value = newValue
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