package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.BottomSheetScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for text to speech configuration
 * shows if web server is enabled
 */
@Composable
fun WebserverConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.webserver,
        secondaryText = viewModel.isHttpServerEnabled.flow.collectAsState().value.toText(),
        screen = BottomSheetScreens.Webserver
    )
}

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Composable
fun WebserverConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.webserver) {

        SwitchListItem(
            text = MR.strings.enableHTTPApi,
            isChecked = viewModel.isHttpServerEnabled.flow.collectAsState().value,
            onCheckedChange = viewModel.isHttpServerEnabled::set
        )

        TextFieldListItem(
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = viewModel.httpServerPort.flow.collectAsState().value,
            onValueChange = viewModel.httpServerPort::set
        )

        WebserverSSL(viewModel)
    }

}

@Composable
private fun WebserverSSL(viewModel: ConfigurationScreenViewModel) {

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isHttpServerEnabled.flow.collectAsState().value
    ) {

        Column {

            val isHttpServerSSLValue by viewModel.isHttpServerSSLEnabled.flow.collectAsState()

            SwitchListItem(
                text = MR.strings.enableSSL,
                isChecked = isHttpServerSSLValue,
                onCheckedChange = viewModel.isHttpServerSSLEnabled::set
            )

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isHttpServerSSLValue
            ) {
                OutlineButtonListItem(
                    text = MR.strings.chooseCertificate,
                    onClick = { })
            }

        }

    }

}
