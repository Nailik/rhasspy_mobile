package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.WebserverConfigurationViewModel

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Preview
@Composable
fun WebServerConfigurationContent(viewModel: WebserverConfigurationViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //switch to enable http server
        SwitchListItem(
            text = MR.strings.enableHTTPApi,
            isChecked = viewModel.isHttpServerEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpServerEnabled
        )

        //port of server
        TextFieldListItem(
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = viewModel.httpServerPort.collectAsState().value,
            onValueChange = viewModel::changeHttpServerPort
        )

        WebserverSSL(viewModel)

    }

}

/**
 * SSL Settings
 * ON/OFF
 * certificate selection
 */
@Composable
private fun WebserverSSL(viewModel: WebserverConfigurationViewModel) {

    //visibility of server settings
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isHttpServerSettingsVisible.collectAsState().value
    ) {

        Column {

            //switch to enabled http ssl
            SwitchListItem(
                text = MR.strings.enableSSL,
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
                OutlineButtonListItem(
                    text = MR.strings.chooseCertificate,
                    onClick = { })

            }

        }

    }

}
