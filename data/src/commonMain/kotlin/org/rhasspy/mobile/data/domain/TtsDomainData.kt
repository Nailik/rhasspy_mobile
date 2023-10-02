package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import kotlin.time.Duration

@Serializable
data class TtsDomainData(
    val option: TtsDomainOption,
    val rhasspy2HermesMqttTimeout: Duration,
)