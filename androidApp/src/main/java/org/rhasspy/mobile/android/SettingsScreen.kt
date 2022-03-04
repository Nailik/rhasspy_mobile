package org.rhasspy.mobile.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.data.Language
import org.rhasspy.mobile.data.Theme

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        LanguageItem()
        Divider()
        ThemeItem()
        Divider()
        AutomaticSilenceDetectionItem()
        Divider()
        BackgroundWakeWordDetectionItem()
        Divider()
        WakeWordIndicationItem()
        Divider()
        ShowLogItem()
        Divider()
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LanguageItem() {
    DropDownEnumListItem(Language.English) { Language.values() }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeItem() {
    DropDownEnumListItem(Theme.System) { Theme.values() }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <E : DataEnum> DropDownEnumListItem(initial: E, values: () -> Array<E>) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(initial) }

    ListItem(modifier = Modifier
        .clickable { expanded = true },
        text = { Text(selected.text) },
        trailing = {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = MR.strings.wakeUp,
            )
        })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            values().forEach {
                DropdownMenuItem(
                    text = { Text(it.text) },
                    onClick = { selected = it })
            }
        }
    }
}

@Composable
fun AutomaticSilenceDetectionItem() {

}

@Composable
fun BackgroundWakeWordDetectionItem() {

}

@Composable
fun WakeWordIndicationItem() {

}

@Composable
fun ShowLogItem() {

}