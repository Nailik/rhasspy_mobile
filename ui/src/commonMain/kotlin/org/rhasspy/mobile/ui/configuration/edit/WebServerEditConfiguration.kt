package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemEdit
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.*
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Action.OpenWebServerSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.WebServerConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Edit

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Composable
fun WebServerConfigurationEditContent() {
    val viewModel: WebServerConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationEditViewState.collectAsState()

    ConfigurationScreenItemEdit(
        modifier = Modifier.testTag(WebServerConfigurationScreen(Edit)),
        kViewModel = viewModel,
        title = MR.strings.webserver.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                //switch to enable http server
                SwitchListItem(
                    text = MR.strings.enableHTTPApi.stable,
                    modifier = Modifier.testTag(TestTag.ServerSwitch),
                    isChecked = viewState.editData.isHttpServerEnabled,
                    onCheckedChange = { viewModel.onEvent(SetHttpServerEnabled(it)) }
                )
            }

            item {
                //visibility of server settings
                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = viewState.editData.isHttpServerEnabled
                ) {

                    Column {

                        //port of server
                        TextFieldListItem(
                            label = MR.strings.port.stable,
                            modifier = Modifier.testTag(TestTag.Port),
                            value = viewState.editData.httpServerPortText,
                            isLastItem = true,
                            onValueChange = { viewModel.onEvent(UpdateHttpServerPort(it)) },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        WebserverSSL(
                            isHttpServerSSLEnabled = viewState.editData.isHttpServerSSLEnabled,
                            httpServerSSLKeyStoreFileName = viewState.editData.httpServerSSLKeyStoreFileName,
                            httpServerSSLKeyStorePassword = viewState.editData.httpServerSSLKeyStorePassword,
                            httpServerSSLKeyAlias = viewState.editData.httpServerSSLKeyAlias,
                            httpServerSSLKeyPassword = viewState.editData.httpServerSSLKeyPassword,
                            onAction = viewModel::onEvent
                        )

                    }

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
private fun WebserverSSL(
    isHttpServerSSLEnabled: Boolean,
    httpServerSSLKeyStoreFileName: String?,
    httpServerSSLKeyStorePassword: String,
    httpServerSSLKeyAlias: String,
    httpServerSSLKeyPassword: String,
    onAction: (WebServerConfigurationUiEvent) -> Unit
) {


    //switch to enabled http ssl
    SwitchListItem(
        text = MR.strings.enableSSL.stable,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = isHttpServerSSLEnabled,
        onCheckedChange = { onAction(SetHttpServerSSLEnabled(it)) }
    )

    //visibility of choose certificate button for ssl
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isHttpServerSSLEnabled
    ) {

        Column {

            ListElement(
                modifier = Modifier
                    .testTag(TestTag.WebServerSSLWiki)
                    .clickable(onClick = { onAction(OpenWebServerSSLWiki) }),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = MR.strings.sslWiki.stable
                    )
                },
                text = { Text(MR.strings.sslWiki.stable) },
                secondaryText = { Text(MR.strings.sslWikiInfo.stable) }
            )

            //button to select ssl certificate
            FilledTonalButtonListItem(
                text = MR.strings.chooseCertificate.stable,
                modifier = Modifier.testTag(TestTag.CertificateButton),
                onClick = { onAction(SelectSSLCertificate) }
            )

            val isKeyStoreFileTextVisible by remember { derivedStateOf { httpServerSSLKeyStoreFileName != null } }

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isKeyStoreFileTextVisible
            ) {

                val keyStoreFileText by remember { derivedStateOf { httpServerSSLKeyStoreFileName ?: "" } }

                InformationListElement(
                    text = translate(resource = MR.strings.currentlySelectedCertificate.stable, keyStoreFileText)
                )
            }

            //text field to change key store password
            TextFieldListItemVisibility(
                label = MR.strings.keyStorePassword.stable,
                value = httpServerSSLKeyStorePassword,
                onValueChange = { onAction(UpdateHttpSSLKeyStorePassword(it)) },
                isLastItem = false
            )

            //textField to change key alias
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyAlias.stable,
                value = httpServerSSLKeyAlias,
                onValueChange = { onAction(UpdateHttpSSLKeyAlias(it)) },
                isLastItem = false
            )

            //textField to change key password
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyPassword.stable,
                value = httpServerSSLKeyPassword,
                onValueChange = { onAction(UpdateHttpSSLKeyPassword(it)) },
            )

        }
    }

}