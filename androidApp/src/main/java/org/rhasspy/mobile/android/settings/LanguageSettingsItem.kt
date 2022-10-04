package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun LanguageSettingsItem(viewModel: SettingsScreenViewModel) {
    DropDownEnumListItem(
        selected = viewModel.currentLanguage.collectAsState().value,
        values = viewModel.languageOptions,
        onSelect = viewModel::selectLanguage
    )
}