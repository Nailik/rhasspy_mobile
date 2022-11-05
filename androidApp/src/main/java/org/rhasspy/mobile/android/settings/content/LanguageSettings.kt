package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.viewModels.settings.DeviceSettingsSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.LanguageSettingsViewModel

@Preview
@Composable
fun LanguageSettingsScreenItemContent(viewModel: LanguageSettingsViewModel = viewModel()) {

    SettingsScreenItemContent(MR.strings.language) {

        RadioButtonsEnumSelection(
            selected = viewModel.languageOption.collectAsState().value,
            onSelect = viewModel::selectLanguageOption,
            values = viewModel.languageOptions
        )

    }

}