package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.viewModels.settings.LanguageSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.ThemeSettingsViewModel

@Preview
@Composable
fun ThemeSettingsScreenItemContent(viewModel: ThemeSettingsViewModel = viewModel()) {

    SettingsScreenItemContent(MR.strings.theme) {

        RadioButtonsEnumSelection(
            selected = viewModel.themeOption.collectAsState().value,
            onSelect = viewModel::selectThemeOption,
            values = viewModel.themeOptions
        )

    }

}