package org.rhasspy.mobile.data.pipeline

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.DialogManagementOption

@Serializable
data class PipelineData(
    val option: DialogManagementOption,
    val asrDomainTimeout: Long,
    val intentDomainTimeout: Long,
)