package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
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
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.configuration.WebServerConfigurationViewModel

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Preview
@Composable
fun WebServerConfigurationContent(viewModel: WebServerConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.WebServerConfiguration),
        title = MR.strings.webserver,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //switch to enable http server
            SwitchListItem(
                text = MR.strings.enableHTTPApi,
                modifier = Modifier.testTag(TestTag.ServerSwitch),
                isChecked = viewModel.isHttpServerEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHttpServerEnabled
            )
        }

        item {
            //visibility of server settings
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewModel.isHttpServerSettingsVisible.collectAsState().value
            ) {

                Column {

                    //port of server
                    TextFieldListItem(
                        label = MR.strings.port,
                        modifier = Modifier.testTag(TestTag.Port),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = viewModel.httpServerPortText.collectAsState().value,
                        onValueChange = viewModel::changeHttpServerPort
                    )

                    WebserverSSL(viewModel)

                }

            }
        }
    }

}

//TODO information how to create certificate
/**
 * SSL Settings
 * ON/OFF
 * certificate selection
 */
@Composable
private fun WebserverSSL(viewModel: WebServerConfigurationViewModel) {

    //switch to enabled http ssl
    SwitchListItem(
        text = MR.strings.enableSSL,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = viewModel.isHttpServerSSLEnabled.collectAsState().value,
        onCheckedChange = viewModel::toggleHttpServerSSLEnabled
    )

    //visibility of choose certificate button for ssl
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isHttpServerSSLCertificateVisible.collectAsState().value
    ) {

        //button to select ssl certificate
        FilledTonalButtonListItem(
            text = MR.strings.chooseCertificate,
            modifier = Modifier.testTag(TestTag.CertificateButton),
            onClick = { })

    }

}

/**
 * test button to start webserver test
 */
@Composable
private fun TestContent(
    viewModel: WebServerConfigurationViewModel
) {
    FilledTonalButtonListItem(
        text = MR.strings.executeStartWebserver,
        onClick = viewModel::runTest
    )
}