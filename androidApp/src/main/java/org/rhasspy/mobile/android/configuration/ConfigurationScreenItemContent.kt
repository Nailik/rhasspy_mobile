package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text

/**
 * Content of Configuration Screen Item
 *
 * AppBar with Back button and title
 * BottomBar with Save, Discard actions and test FAB
 *
 * Shows dialog on Back press when there are unsaved changes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenItemContent(
    modifier: Modifier,
    title: StringResource,
    hasUnsavedChanges: StateFlow<Boolean>,
    onSave: () -> Unit,
    onTest: () -> Unit,
    onDiscard: () -> Unit,
    Content: @Composable ColumnScope.() -> Unit
) {

    val navigation = LocalMainNavController.current

    var showDialog by rememberSaveable { mutableStateOf(false) }

    val hasUnsavedChangesValue by hasUnsavedChanges.collectAsState()

    val onBackPress: () -> Unit = {
        if (hasUnsavedChangesValue) {
            showDialog = true
        } else {
            navigation.popBackStack()
        }
    }

    //Back handler to show dialog if there are unsaved changes
    BackHandler(onBack = onBackPress)

    //Show unsaved changes dialog
    if (showDialog) {
        UnsavedChangesDialog(
            onDismissRequest = {
                //close dialog on outside click
                showDialog = false
            },
            onResult = { result ->
                if (result) {
                    //save changes
                    onSave.invoke()
                }
                //close dialog and go back
                showDialog = false
                navigation.popBackStack()
            },
        )
    }

    //appbar, bottomAppBar, content
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTag.ConfigurationScreenItemContent),
        topBar = {
            AppBar(
                title = title,
                onBackClick = onBackPress
            )
        },
        bottomBar = {
            BottomAppBar(
                hasUnsavedChanges = hasUnsavedChangesValue,
                onSave = onSave,
                onTest = onTest,
                onDiscard = onDiscard
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Content()
        }
    }
}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun UnsavedChangesDialog(onDismissRequest: () -> Unit, onResult: (result: Boolean) -> Unit) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onResult(true) }) {
                Text(MR.strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(false) }) {
                Text(MR.strings.discard)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.discard
            )
        },
        title = { Text(MR.strings.unsavedChanges) },
        text = { Text(MR.strings.unsavedChangesInformation) }
    )

}

/**
 * bottom app bar
 * discard, save actions
 * fab for testing
 */
@Composable
private fun BottomAppBar(
    hasUnsavedChanges: Boolean,
    onSave: () -> Unit,
    onTest: () -> Unit,
    onDiscard: () -> Unit
) {

    BottomAppBar(
        actions = {
            IconButton(onClick = onDiscard, enabled = hasUnsavedChanges) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.discard,
                )
            }
            IconButton(onClick = onSave, enabled = hasUnsavedChanges) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = MR.strings.save
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTest,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = MR.strings.test
                )
            }
        }
    )

}

/**
 * top app bar with title and back navigation button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: StringResource, onBackClick: () -> Unit) {

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )

}
