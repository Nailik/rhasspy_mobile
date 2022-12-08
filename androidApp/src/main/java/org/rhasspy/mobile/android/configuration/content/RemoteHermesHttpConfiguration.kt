package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.configuration.RemoteHermesHttpConfigurationViewModel

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = getViewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.RemoteHermesHttpConfiguration),
        title = MR.strings.remoteHermesHTTP,
        viewModel = viewModel,
        hasTestButton = false
    ) {

        //base http endpoint
        TextFieldListItem(
            label = MR.strings.baseHost,
            modifier = Modifier.testTag(TestTag.Host),
            value = viewModel.httpServerEndpoint.collectAsState().value,
            onValueChange = viewModel::updateHttpServerEndpoint,
        )

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            modifier = Modifier.testTag(TestTag.SSLSwitch),
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = viewModel.isHttpSSLVerificationDisabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpSSLVerificationDisabled
        )

    }

}