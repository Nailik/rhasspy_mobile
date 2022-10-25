package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.configuration.RemoteHermesHttpConfigurationViewModel

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.RemoteHermesHttpConfiguration),
        title = MR.strings.remoteHermesHTTP,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) {

        //switch to toggle validation of SSL certificate
        //TODO text
        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = viewModel.isHttpSSLVerificationEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpSSLVerificationEnabled
        )

        //TODO http endpoint settings and then inerhit on enpoints with option to set custom

    }

}