package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.LanguageSettingsScreen
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel

/**
 * language setting screen
 * radio button list with different languages
 */

@Composable
fun LanguageSettingsScreenItemContent() {
    val viewModel: LanguageSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        SettingsScreenItemContent(
            modifier = Modifier.testTag(LanguageSettingsScreen),
            title = MR.strings.language.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            RadioButtonsEnumSelectionList(
                selected = viewState.languageOption,
                onSelect = { viewModel.onEvent(SelectLanguageOption(it)) },
                values = viewState.languageOptions
            )

        }
    }

}