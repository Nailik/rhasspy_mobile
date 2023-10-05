package org.rhasspy.mobile.viewmodel.configuration.tts

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.TtsDomainOption

@Stable
data class TtsDomainConfigurationViewState internal constructor(
    val editData: TtsDomainConfigurationData
) {

    @Stable
    data class TtsDomainConfigurationData(
        val ttsDomainOption: TtsDomainOption,
        val rhasspy2HermesMqttTimeout: String,
    ) {

        val ttsDomainOptions: ImmutableList<TtsDomainOption> = TtsDomainOption.entries.toImmutableList()

    }

}