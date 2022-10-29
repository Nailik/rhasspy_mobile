package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

/**
 * Custom keywords screen
 */
@Composable
fun PorcupineKeywordCustomScreen(viewModel: WakeWordConfigurationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            //added files
            val options by viewModel.wakeWordPorcupineKeywordCustomOptions.collectAsState()
            options.forEachIndexed { index, element ->

                CustomKeywordListItem(
                    element = element,
                    onClick = {
                        viewModel.clickPorcupineKeywordCustom(index)
                    },
                    onToggle = { enabled ->
                        viewModel.togglePorcupineKeywordCustom(index, enabled)
                    },
                    onDelete = {
                        viewModel.deletePorcupineKeywordCustom(index)
                    },
                    onUpdateSensitivity = { sensitivity ->
                        viewModel.updateWakeWordPorcupineKeywordCustomSensitivity(index, sensitivity)
                    }
                )

                CustomDivider()

            }

            //removed files (not yet saved)
            val optionsRemoved by viewModel.wakeWordPorcupineKeywordCustomOptionsRemoved.collectAsState()
            optionsRemoved.forEachIndexed { index, element ->

                CustomKeywordDeletedListItem(
                    element = element,
                    onUndo = { viewModel.undoWakeWordPorcupineCustomKeywordDeleted(index) }
                )

                CustomDivider()

            }

        }

        CustomKeywordsActionButtons(modifier = Modifier.align(Alignment.End), viewModel = viewModel)

    }

}

/**
 * list item for active custom keywords
 * enable keyword
 * sensitivity
 * delete keyword
 */
@Composable
private fun CustomKeywordListItem(
    element: PorcupineCustomKeyword,
    onClick: () -> Unit,
    onToggle: (enabled: Boolean) -> Unit,
    onDelete: () -> Unit,
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
            Text(element.fileName)
        },
        trailing = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.defaultText
                )
            }
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

/**
 * list item for deleted custom keywords but not yet saved
 * contains quick action to undo deletion
 */
@Composable
private fun CustomKeywordDeletedListItem(
    element: PorcupineCustomKeyword,
    onUndo: () -> Unit,
) {
    ListElement(
        text = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = element.fileName
            )
        },
        trailing = {
            IconButton(onClick = onUndo) {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = MR.strings.defaultText
                )
            }
        }
    )
}

/**
 * custom keywords action buttons (download and open file)
 */
@Composable
private fun CustomKeywordsActionButtons(modifier: Modifier, viewModel: WakeWordConfigurationViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        FilledTonalButton(
            onClick = viewModel::downloadCustomPorcupineKeyword,
            content = {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = MR.strings.fileOpen
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(MR.strings.download)
            })

        FilledTonalButton(
            onClick = viewModel::addCustomPorcupineKeyword,
            content = {
                Icon(
                    imageVector = Icons.Filled.FileOpen,
                    contentDescription = MR.strings.fileOpen
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(MR.strings.fileOpen)
            })

    }
}