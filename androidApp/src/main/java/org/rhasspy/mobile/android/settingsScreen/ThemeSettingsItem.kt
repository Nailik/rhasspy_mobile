package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun ThemeSettingsItem(viewModel: SettingsScreenViewModel) {
    DropDownEnumListItem(
        selected = viewModel.currentTheme.collectAsState().value,
        values = viewModel.themeOptions,
        onSelect = viewModel::selectTheme
    )
}