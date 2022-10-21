package org.rhasspy.mobile.android.ui.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.configuration.RemoteHermesHttpConfigurationViewModel


/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation,
            secondaryText = MR.strings.disableSSLValidationInformation,
            isChecked = !viewModel.isHttpSSLVerificationEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleHttpSSLVerificationEnabled
        )

    }

}