package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.settings.ThemeSettingsViewModel

@Preview
@Composable
fun ThemeSettingsScreenItemContent(viewModel: ThemeSettingsViewModel = getViewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.ThemeSettings),
        title = MR.strings.theme
    ) {

        RadioButtonsEnumSelection(
            selected = viewModel.themeOption.collectAsState().value,
            onSelect = viewModel::selectThemeOption,
            values = viewModel.themeOptions
        )

    }

}