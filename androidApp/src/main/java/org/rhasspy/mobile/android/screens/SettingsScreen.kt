package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.android.settings.AboutSettingsItem
import org.rhasspy.mobile.android.settings.AutomaticSilenceDetectionItem
import org.rhasspy.mobile.android.settings.BackgroundServiceItem
import org.rhasspy.mobile.android.settings.DeviceSettingsItem
import org.rhasspy.mobile.android.settings.LanguageSettingsItem
import org.rhasspy.mobile.android.settings.MicrophoneOverlayItem
import org.rhasspy.mobile.android.settings.ProblemHandlingSettingsItem
import org.rhasspy.mobile.android.settings.SaveAndRestoreSettingsItem
import org.rhasspy.mobile.android.settings.ShowLogSettingsItem
import org.rhasspy.mobile.android.settings.SoundsItem
import org.rhasspy.mobile.android.settings.ThemeSettingsItem
import org.rhasspy.mobile.android.settings.WakeWordIndicationItem
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

/**
 * screen with list that open settings bottom sheet
 */
@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LanguageSettingsItem(viewModel)
        CustomDivider()
        ThemeSettingsItem(viewModel)
        CustomDivider()
        BackgroundServiceItem(viewModel)
        CustomDivider()
        MicrophoneOverlayItem(viewModel)
        CustomDivider()
        WakeWordIndicationItem(viewModel)
        CustomDivider()
        SoundsItem(viewModel)
        CustomDivider()
        DeviceSettingsItem(viewModel)
        CustomDivider()
        AutomaticSilenceDetectionItem(viewModel)
        CustomDivider()
        ShowLogSettingsItem(viewModel)
        CustomDivider()
        ProblemHandlingSettingsItem(viewModel)
        CustomDivider()
        SaveAndRestoreSettingsItem(viewModel)
        CustomDivider()
        AboutSettingsItem()
    }
}



