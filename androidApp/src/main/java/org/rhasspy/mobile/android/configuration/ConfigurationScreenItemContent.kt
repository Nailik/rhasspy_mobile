package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            onSave = {
                showDialog = false
                onSave.invoke()
                navigation.popBackStack()
            },
            onDiscard = {
                showDialog = false
                onDiscard.invoke()
                navigation.popBackStack()
            }
        )
    }

    //appbar, bottomAppBar, content

    Surface(tonalElevation = 1.dp) {
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
}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun UnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onSave,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.save)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard,
                modifier = Modifier.testTag(TestTag.DialogCancel)
            ) {
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
        text = { Text(MR.strings.unsavedChangesInformation) },
        modifier = Modifier.testTag(TestTag.DialogUnsavedChanges)
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
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = onDiscard,
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.discard,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = onSave,
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = MR.strings.save
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarTest),
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
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
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
