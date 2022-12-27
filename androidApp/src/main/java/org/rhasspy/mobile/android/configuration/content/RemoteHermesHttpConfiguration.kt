package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
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
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.RemoteHermesHttpConfiguration),
        title = MR.strings.remoteHermesHTTP,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //base http endpoint
            TextFieldListItem(
                label = MR.strings.baseHost,
                modifier = Modifier.testTag(TestTag.Host),
                value = viewModel.httpServerEndpointHost.collectAsState().value,
                onValueChange = viewModel::updateHttpServerEndpointHost,
                isLastItem = false
            )
        }

        item {
            //port
            TextFieldListItem(
                label = MR.strings.port,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag(TestTag.Port),
                value = viewModel.httpServerEndpointPort.collectAsState().value,
                onValueChange = viewModel::updateHttpServerEndpointPort,
            )
        }

        item {
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

}

/**
 * test http connection button
 */
@Composable
private fun TestContent(
    viewModel: RemoteHermesHttpConfigurationViewModel
) {
    FilledTonalButtonListItem(
        text = MR.strings.executeTestHttpConnection,
        onClick = viewModel::runTest
    )
}