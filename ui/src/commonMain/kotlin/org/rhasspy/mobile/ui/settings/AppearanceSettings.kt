package org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.AppearanceSettingsScreen
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectThemeOption
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsViewModel

/**
 * language setting screen
 * radio button list with different languages
 */

@Composable
fun AppearanceSettingsScreenItemContent() {
    val viewModel: AppearanceSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(
        modifier = Modifier.testTag(AppearanceSettingsScreen),
        screenViewModel = viewModel
    ) {

        val viewState by viewModel.viewState.collectAsState()

        SettingsScreenItemContent(
            title = MR.strings.theme_and_language.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                ListElement {
                    Text(resource = MR.strings.language.stable)
                }

                RadioButtonsEnumSelectionList(
                    selected = viewState.languageOption,
                    onSelect = { viewModel.onEvent(SelectLanguageOption(it)) },
                    values = viewState.languageOptions
                )

            }

            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                ListElement {
                    Text(resource = MR.strings.theme.stable)
                }

                RadioButtonsEnumSelectionList(
                    selected = viewState.themeOption,
                    onSelect = { viewModel.onEvent(SelectThemeOption(it)) },
                    values = viewState.themeOptions
                )

            }
        }
    }

}