package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.combinedTestTag
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.viewmodel.configuration.WakeWordConfigurationViewModel

/**
 * Custom keywords screen
 */
@Composable
fun PorcupineKeywordCustomScreen(viewModel: WakeWordConfigurationViewModel) {

    Column(
        modifier = Modifier
            .testTag(TestTag.PorcupineKeywordCustomScreen)
            .fillMaxSize()
    ) {
        //added files
        val options by viewModel.wakeWordPorcupineKeywordCustomOptions.collectAsState()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            stickyHeader {
                InformationListElement(text = MR.strings.porcupineLanguageInformation)
            }

            items(
                count = options.size,
                itemContent = { index ->
                    KeywordListItem(options.elementAt(index), index, viewModel)
                    CustomDivider()
                })
        }

        CustomKeywordsActionButtons(viewModel = viewModel)

    }

}

/**
 * one keyword in a list
 */
@Composable
private fun KeywordListItem(
    element: WakeWordConfigurationViewModel.PorcupineCustomKeywordUi,
    index: Int,
    viewModel: WakeWordConfigurationViewModel
) {
    if (element.deleted) {
        //small item to be deleted
        CustomKeywordDeletedListItem(
            modifier = Modifier.testTag(element.keyword.fileName),
            keyword = element.keyword,
            onUndo = { viewModel.undoWakeWordPorcupineCustomKeywordDeleted(index) }
        )
    } else {
        //normal item
        CustomKeywordListItem(
            modifier = Modifier.testTag(element.keyword.fileName),
            keyword = element.keyword,
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
    modifier: Modifier = Modifier,
    keyword: PorcupineCustomKeyword,
    onClick: () -> Unit,
    onToggle: (enabled: Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdateSensitivity: (sensitivity: Float) -> Unit,
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = {
            Checkbox(
                checked = keyword.isEnabled,
                onCheckedChange = onToggle
            )
        },
        text = {
            Text(keyword.fileName)
        },
        trailing = {
            IconButton(
                modifier = Modifier.combinedTestTag(keyword.fileName, TestTag.Delete),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.defaultText
                )
            }
        }
    )

    if (keyword.isEnabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = MR.strings.sensitivity,
            value = keyword.sensitivity,
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
    modifier: Modifier = Modifier,
    keyword: PorcupineCustomKeyword,
    onUndo: () -> Unit,
) {
    ListElement(
        modifier = modifier,
        text = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = keyword.fileName
            )
        },
        trailing = {
            IconButton(
                modifier = Modifier.combinedTestTag(keyword.fileName, TestTag.Undo),
                onClick = onUndo
            ) {
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
private fun CustomKeywordsActionButtons(viewModel: WakeWordConfigurationViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        FilledTonalButton(
            modifier = Modifier
                .weight(1f)
                .testTag(TestTag.Download),
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
            modifier = Modifier
                .weight(1f)
                .testTag(TestTag.SelectFile),
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