package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Edit
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType.Test

/**
 * Content of Configuration Screen Item
 *
 * AppBar with Back button and title
 * BottomBar with Save, Discard actions and test FAB
 *
 * Shows dialog on Back press when there are unsaved changes
 */
@Composable
fun ConfigurationScreenItemContent(
    modifier: Modifier,
    title: StableStringResource,
    editContent: LazyListScope.() -> Unit,
    testContent: @Composable () -> Unit,
    screenType: ConfigurationScreenDestinationType,
    editViewModel: () -> IConfigurationEditViewModel<*>,
    testViewModel: () -> IConfigurationTestViewModel
) {

    Box(modifier = modifier) {
        when (screenType) {
            Edit -> {
                val viewModel = editViewModel()

                ConfigurationScreenItemEdit(
                    title = title,
                    viewState = viewModel.configurationEditViewState.collectAsState().value,
                    onEvent = viewModel::onEvent,
                    content = editContent
                )
            }

            Test -> {
                val viewModel = testViewModel()

                ConfigurationScreenTest(
                    title = title,
                    viewState = viewModel.configurationTestViewState.collectAsState().value,
                    onEvent = viewModel::onEvent,
                    content = testContent
                )
            }
        }
    }

}
