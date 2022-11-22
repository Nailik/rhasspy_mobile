package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import org.koin.core.qualifier.named
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.test.EventListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.FilledTonalButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.serviceModule
import org.rhasspy.mobile.viewModels.configuration.WebServerConfigurationViewModel

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Preview
@Composable
fun WebServerConfigurationContent(viewModel: WebServerConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.WebServerConfiguration),
        title = MR.strings.webserver,
        viewModel = viewModel,
        testContent = { modifier ->
            Koin(appDeclaration = { modules(serviceModule) }) {
                TestContent(modifier, viewModel)
            }
        }
    ) {

        //switch to enable http server
        SwitchListItem(
            text = MR.strings.enableHTTPApi,
            modifier = Modifier.testTag(TestTag.ServerSwitch),
            isChecked = viewModel.isHttpServerEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpServerEnabled
        )

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

//TODO new page instead of bottom sheet
@Composable
private fun TestContent(
    modifier: Modifier, viewModel: WebServerConfigurationViewModel) {

        val eventLogger = get<EventLogger>(named(EventTag.WebServer.name))

        Column(
            modifier = modifier
                .heightIn(min = 400.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            val receivingStateList by eventLogger.events.collectAsState()
            receivingStateList.forEach {
                EventListItem(it)
        }
    }

}