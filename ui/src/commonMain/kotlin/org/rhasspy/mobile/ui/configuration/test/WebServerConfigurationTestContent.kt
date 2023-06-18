package org.rhasspy.mobile.ui.configuration.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenTest
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationEditViewModel

@Composable
fun WebServerConfigurationTestContent() {
    val viewModel: WebServerConfigurationTestViewModel = LocalViewModelFactory.current.getViewModel()

    ConfigurationScreenTest(
        title = title,
        viewState = viewModel.configurationTestViewState.collectAsState().value,
        onEvent = viewModel::onEvent,
        content = testContent
    )
}