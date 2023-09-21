package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData

class DialogManagementConfigurationDataMapper {

    operator fun invoke(data: PipelineData): DialogManagementConfigurationData {
        return DialogManagementConfigurationData(
            dialogManagementOption = data.option,
            textAsrTimeout = data.asrDomainTimeout,
            intentRecognitionTimeout = data.intentDomainTimeout,
        )
    }

    operator fun invoke(data: DialogManagementConfigurationData): PipelineData {
        return PipelineData(
            option = data.dialogManagementOption,
            asrDomainTimeout = data.textAsrTimeout,
            intentDomainTimeout = data.intentRecognitionTimeout,
        )
    }

}