package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

/**
 * default keywords screen
 */
@Composable
fun PorcupineKeywordDefaultScreen(viewModel: WakeWordConfigurationViewModel) {

    val options by viewModel.wakeWordPorcupineKeywordDefaultOptions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .testTag(TestTag.PorcupineKeywordDefaultScreen)
            .fillMaxHeight()
    ) {
        items(
            count = options.size,
            itemContent = { index ->

                val element = options.elementAt(index)

                DefaultKeywordListItem(element = element,
                    onClick = { viewModel.clickPorcupineKeywordDefault(index) },
                    onToggle = { enabled -> viewModel.togglePorcupineKeywordDefault(index, enabled) },
                    onUpdateSensitivity = { sensitivity -> viewModel.updateWakeWordPorcupineKeywordDefaultSensitivity(index, sensitivity) })

                CustomDivider()
            }
        )
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
        modifier = Modifier.clickable(onClick = onClick),
        icon = {
            Checkbox(
                checked = element.enabled,
                onCheckedChange = onToggle
            )
        },
        text = {
            Text(element.option.text)
        }
    )

    if (element.enabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = MR.strings.sensitivity,
            value = element.sensitivity,
            onValueChange = onUpdateSensitivity
        )
    }
}