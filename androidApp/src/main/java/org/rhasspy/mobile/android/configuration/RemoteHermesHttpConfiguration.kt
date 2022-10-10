package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.BottomSheetScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for http configuration
 * shows if ssl verification is enabled
 */
@Composable
fun RemoteHermesHttpConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.remoteHermesHTTP,
        secondaryText = "${translate(MR.strings.sslValidation)} ${
            translate(
                viewModel.isHttpSSLVerificationEnabled.flow.collectAsState().value
                    .toText()
            )
        }",
        screen = BottomSheetScreens.RemoteHermesHTTP
    )

}

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.remoteHermesHTTP) {

        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = !viewModel.isHttpSSLVerificationEnabled.flow.collectAsState().value,
            onCheckedChange = viewModel.isHttpSSLVerificationEnabled::set
        )

    }

}