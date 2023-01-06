package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
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
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.*
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.configuration.WebServerConfigurationViewModel

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
        modifier = Modifier.testTag(ConfigurationScreenType.WebServerConfiguration),
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

        Column {

            ListElement(
                modifier = Modifier
                    .testTag(TestTag.WebServerSSLWiki)
                    .clickable(onClick = viewModel::openWebServerSSLWiki),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = MR.strings.sslWiki
                    )
                },
                text = { Text(MR.strings.sslWiki) },
                secondaryText = { Text(MR.strings.sslWikiInfo) }
            )

            //button to select ssl certificate
            FilledTonalButtonListItem(
                text = MR.strings.chooseCertificate,
                modifier = Modifier.testTag(TestTag.CertificateButton),
                onClick = viewModel::selectSSLCertificate
            )

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewModel.isHttpServerSSLKeyStoreFileTextVisible.collectAsState().value
            ) {
                InformationListElement(
                    text = translate(
                        resource = MR.strings.currentlySelectedCertificate,
                        viewModel.httpServerSSLKeyStoreFileText.collectAsState().value
                    )
                )
            }

            //text field to change key store password
            TextFieldListItemVisibility(
                label = MR.strings.keyStorePassword,
                value = viewModel.httpServerSSLKeyStorePassword.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyStorePassword,
                isLastItem = false
            )

            //textField to change key alias
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyAlias,
                value = viewModel.httpServerSSLKeyAlias.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyAlias,
                isLastItem = false
            )

            //textField to change key password
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyPassword,
                value = viewModel.httpServerSSLKeyPassword.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyPassword
            )

        }
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
        onClick = viewModel::runWebServerTest
    )
}