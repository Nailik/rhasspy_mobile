package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel

/**
 * language setting screen
 * radio button list with different languages
 */
@Preview
@Composable
fun LanguageSettingsScreenItemContent(viewModel: LanguageSettingsViewModel = get()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.LanguageSettings),
        title = MR.strings.language.stable
    ) {

        RadioButtonsEnumSelection(
            selected = viewModel.languageOption.collectAsState().value,
            onSelect = viewModel::selectLanguageOption,
            values = viewModel.languageOptions
        )

    }

}