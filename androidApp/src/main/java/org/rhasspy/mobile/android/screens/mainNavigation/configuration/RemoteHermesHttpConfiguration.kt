package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.configuration.RemoteHermesHttpConfigurationViewModel


/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = viewModel()) {

    PageContent(MR.strings.remoteHermesHTTP) {

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = !viewModel.isHttpSSLVerificationEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpSSLVerificationEnabled
        )

    }

}