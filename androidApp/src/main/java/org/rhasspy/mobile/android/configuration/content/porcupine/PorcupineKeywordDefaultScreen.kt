package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.combinedTestTag
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.ClickPorcupineKeywordDefault
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.TogglePorcupineKeywordDefault
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.UpdateWakeWordPorcupineKeywordDefaultSensitivity
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState

/**
 * default keywords screen
 */
@Composable
fun PorcupineKeywordDefaultScreen(
    viewState: PorcupineViewState,
    onAction: (PorcupineUiAction) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .testTag(TestTag.PorcupineKeywordDefaultScreen)
            .fillMaxHeight()
    ) {
        items(viewState.defaultOptions) { option ->

            DefaultKeywordListItem(
                element = option,
                onClick = { onAction(ClickPorcupineKeywordDefault(option)) },
                onToggle = { onAction(TogglePorcupineKeywordDefault(option, it)) },
                onUpdateSensitivity = { onAction(UpdateWakeWordPorcupineKeywordDefaultSensitivity(option, it)) }
            )

            CustomDivider()
        }
    }

}

/**
 * list item for default keywords
 * enabled/disabled
 * sensitivity
 */
@Composable
private fun DefaultKeywordListItem(
    element: PorcupineDefaultKeyword,
    onClick: () -> Unit,
    onToggle: (enabled: Boolean) -> Unit,
    onUpdateSensitivity: (sensitivity: Float) -> Unit,
) {
    ListElement(
        modifier = Modifier
            .testTag(IOption = element.option)
            .clickable(onClick = onClick),
        icon = {
            Checkbox(
                checked = element.isEnabled,
                onCheckedChange = onToggle
            )
        },
        text = {
            Text(element.option.text)
        }
    )

    if (element.isEnabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier
                .combinedTestTag(element.option, TestTag.Sensitivity)
                .padding(horizontal = 12.dp),
            text = MR.strings.sensitivity.stable,
            value = element.sensitivity,
            onValueChange = onUpdateSensitivity
        )
    }
}