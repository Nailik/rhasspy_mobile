package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.icons.RhasspyLogo
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel

/**
 * navigation bar on bottom
 */
@Composable
fun BottomNavigation() {

    val viewModel: MainScreenViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    NavigationBar {

        NavigationBarItem(
            modifier = Modifier.testTag(HomeScreen),
            icon = { Icon(Icons.Filled.Mic, MR.strings.home.stable) },
            label = {
                Text(
                    resource = MR.strings.home.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = viewState.bottomNavigationIndex == 0,
            onClick = { viewModel.onEvent(Navigate(HomeScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(DialogScreen),
            icon = { Icon(Icons.Filled.Timeline, MR.strings.home.stable) },
            label = {
                Text(
                    resource = MR.strings.dialog.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = viewState.bottomNavigationIndex == 1,
            onClick = { viewModel.onEvent(Navigate(DialogScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(ConfigurationScreen),
            icon = {
                Icon(
                    RhasspyLogo,
                    MR.strings.configuration.stable,
                    Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    resource = MR.strings.configuration.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = viewState.bottomNavigationIndex == 2,
            onClick = { viewModel.onEvent(Navigate(ConfigurationScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(SettingsScreen),
            icon = { Icon(Icons.Filled.Settings, MR.strings.settings.stable) },
            label = {
                Text(
                    resource = MR.strings.settings.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = viewState.bottomNavigationIndex == 3,
            onClick = { viewModel.onEvent(Navigate(SettingsScreen)) }
        )

        if (viewState.isShowLogEnabled) {
            NavigationBarItem(
                modifier = Modifier.testTag(LogScreen),
                icon = { Icon(Icons.Filled.Code, MR.strings.log.stable) },
                label = {
                    Text(
                        resource = MR.strings.log.stable,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                selected = viewState.bottomNavigationIndex == 4,
                onClick = { viewModel.onEvent(Navigate(LogScreen)) }
            )
        }

    }

}