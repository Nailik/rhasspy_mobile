package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.single
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.test.TestListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.FilledTonalButtonListItem
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

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.WebServerConfiguration),
        title = MR.strings.webserver,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        testingEnabled = viewModel.isTestingEnabled,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard,
        bottomSheetContent = { BottomSheetContent(viewModel) }
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
private fun WebserverSSL(viewModel: WebserverConfigurationViewModel) {

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

@Composable
private fun BottomSheetContent(viewModel: WebserverConfigurationViewModel) {

    val scrollState = rememberScrollState()
    val size = viewModel.currentTestReceivingStateList.collectAsState().value.size

    LaunchedEffect(size){
        scrollState.animateScrollTo(size)
    }

    Column(modifier = Modifier
        .heightIn(min = 400.dp)
        .wrapContentHeight()
        .verticalScroll(scrollState)) {
        val startingState by viewModel.currentTestStartingState.collectAsState()
        TestListItem(startingState)

        val receivingStateList by viewModel.currentTestReceivingStateList.collectAsState()
        receivingStateList.forEach {
            TestListItem(it)
        }
    }

    FilledTonalButtonListItem(text = MR.strings.stop) {

    }

}