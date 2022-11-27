package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.test.EventListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.FilledTonalButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
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
        viewModel = viewModel,
        testContent = { modifier -> TestContent(modifier, viewModel) }
    ) {

        //base http endpoint
        TextFieldListItem(
            label = MR.strings.host,
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

@Composable
private fun TestContent(
    modifier: Modifier,
    viewModel: RemoteHermesHttpConfigurationViewModel
) {
    Column {
        val eventsList by viewModel.events.collectAsState()

        LazyColumn(modifier = modifier.weight(1f)) {
            items(eventsList) { item ->
                EventListItem(item)
            }
        }

        FilledTonalButtonListItem(
            text = MR.strings.test,
            onClick = viewModel::runTest
        )
    }
}