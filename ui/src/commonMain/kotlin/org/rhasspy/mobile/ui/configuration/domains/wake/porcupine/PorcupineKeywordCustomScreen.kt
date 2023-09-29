package org.rhasspy.mobile.ui.configuration.domains.wake.porcupine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.InformationListElement
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SliderListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.AddCustomPorcupineKeyword
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.DownloadCustomPorcupineKeyword
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData

/**
 * Custom keywords screen
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PorcupineKeywordCustomScreen(
    editData: WakeWordPorcupineConfigurationData,
    onEvent: (PorcupineUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .testTag(TestTag.PorcupineKeywordCustomScreen)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            stickyHeader {
                InformationListElement(text = MR.strings.porcupineLanguageInformation.stable)
            }

            items(editData.customOptions) { option ->

                KeywordListItem(
                    option = option,
                    onEvent = onEvent
                )

                CustomDivider()
            }
        }

        CustomKeywordsActionButtons(onAction = onEvent)

    }

}

/**
 * one keyword in a list
 */
@Composable
private fun KeywordListItem(
    option: PorcupineCustomKeyword,
    onEvent: (PorcupineUiEvent) -> Unit
) {
    //normal item
    CustomKeywordListItem(
        modifier = Modifier.testTag(option.fileName),
        keyword = option,
        onClick = { onEvent(ClickPorcupineKeywordCustom(option)) },
        onToggle = { onEvent(SetPorcupineKeywordCustom(option, it)) },
        onDelete = { onEvent(DeletePorcupineKeywordCustom(option)) },
        onUpdateSensitivity = {
            onEvent(
                UpdateWakeWordPorcupineKeywordCustomSensitivity(
                    item = option,
                    value = it.toDouble()
                )
            )
        }
    )
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
                    contentDescription = MR.strings.defaultText.stable
                )
            }
        }
    )

    if (keyword.isEnabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = MR.strings.sensitivity.stable,
            value = keyword.sensitivity.toFloat(),
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
                    contentDescription = MR.strings.defaultText.stable
                )
            }
        }
    )
}

/**
 * custom keywords action buttons (download and open file)
 */
@Composable
private fun CustomKeywordsActionButtons(onAction: (PorcupineUiEvent) -> Unit) {
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
            onClick = { onAction(DownloadCustomPorcupineKeyword) },
            content = {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = MR.strings.fileOpen.stable
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(MR.strings.download.stable)
            }
        )

        FilledTonalButton(
            modifier = Modifier
                .weight(1f)
                .testTag(TestTag.SelectFile),
            onClick = { onAction(AddCustomPorcupineKeyword) },
            content = {
                Icon(
                    imageVector = Icons.Filled.FileOpen,
                    contentDescription = MR.strings.fileOpen.stable
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(MR.strings.fileOpen.stable)
            }
        )

    }
}