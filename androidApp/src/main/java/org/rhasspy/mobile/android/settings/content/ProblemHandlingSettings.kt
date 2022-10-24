package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.settings.ProblemHandlingSettingsViewModel

/**
 * problem handling
 * force cancel enabled
 */
@Preview
@Composable
fun ProblemHandlingSettingsContent(viewModel: ProblemHandlingSettingsViewModel = viewModel()) {

    PageContent(MR.strings.problemHandling) {

        //on/off force cancel
        SwitchListItem(
            text = MR.strings.forceCancel,
            secondaryText = MR.strings.forceCancelInformation,
            isChecked = viewModel.isForceCancelEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleForceCancelEnabled
        )

    }

}