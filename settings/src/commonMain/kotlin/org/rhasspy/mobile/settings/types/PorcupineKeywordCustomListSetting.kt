package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordCustomListSetting(
    key: SettingsEnum,
    initial: ImmutableList<PorcupineCustomKeyword>
) : ISetting<ImmutableList<PorcupineCustomKeyword>>(
    key = key,
    initial = initial
) {

    override fun readValue(): ImmutableList<PorcupineCustomKeyword> {
        return database.settingsPorcupineKeywordListValuesQueries
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
        database.settingsPorcupineKeywordListValuesQueries.transaction {
            newValue.forEach {
                database.settingsPorcupineKeywordListValuesQueries
                    .insertOrUpdate(
                        id = key.name,
                        value = it.fileName,
                        enabled = if (it.isEnabled) 1 else 0,
                        sensitivity = it.sensitivity.toLong()
                    )
            }
        }
    }

}