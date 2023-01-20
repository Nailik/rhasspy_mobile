package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.ServiceStateHeader
import org.rhasspy.mobile.android.content.elements.FloatingActionButton
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.SetSystemColor
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

enum class ConfigurationContentScreens(val route: String) {
    Edit("ConfigurationContentScreens_Edit"),
    Test("ConfigurationContentScreens_Test")
}

/**
 * Content of Configuration Screen Item
 *
 * AppBar with Back button and title
 * BottomBar with Save, Discard actions and test FAB
 *
 * Shows dialog on Back press when there are unsaved changes
 */
@Composable
fun ConfigurationScreenItemContent(
    modifier: Modifier,
    title: StringResource,
    viewModel: IConfigurationViewModel,
    testContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == ConfigurationContentScreens.Edit.route) {
                viewModel.stopTest()
            }
        }
    }

    BackHandler(viewModel.isBackPressDisabled.collectAsState().value) {}

    if (viewModel.isLoading.collectAsState().value) {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    } else {
        CompositionLocalProvider(
            LocalConfigurationNavController provides navController
        ) {
            NavHost(
                navController = navController,
                startDestination = ConfigurationContentScreens.Edit.route,
                modifier = modifier.testTag(TestTag.ConfigurationScreenItemContent),
            ) {
                composable(ConfigurationContentScreens.Edit.route) {
                    EditConfigurationScreen(
                        title = title,
                        viewModel = viewModel,
                        content = content
                    )
                }
                composable(ConfigurationContentScreens.Test.route) {
                    ConfigurationScreenTest(
                        viewModel = viewModel,
                        content = testContent
                    )
                }
            }
        }
    }
}

/**
 * configuration screen where settings are edited
 */
@Composable
private fun EditConfigurationScreen(
    title: StringResource,
    viewModel: IConfigurationViewModel,
    content: LazyListScope.() -> Unit
) {
    SetSystemColor(0.dp)

    val navController = LocalMainNavController.current
    var showBackButtonDialog by rememberSaveable { mutableStateOf(false) }

    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()

    fun onBackPress() {
        if (hasUnsavedChanges) {
            showBackButtonDialog = true
        } else {
            navController.popBackStack()
        }
    }

    //Back handler to show dialog if there are unsaved changes
    BackHandler(onBack = ::onBackPress)

    //Show unsaved changes dialog back press
    if (showBackButtonDialog) {
        UnsavedBackButtonDialog(
            onSave = viewModel::save,
            onDiscard = viewModel::discard,
            onClose = {
                showBackButtonDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                onBackClick = ::onBackPress
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        },
        bottomBar = {
            BottomAppBar(viewModel)
        }
    ) { paddingValues ->
        Surface(tonalElevation = 1.dp) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                stickyHeader {
                    ServiceStateHeader(viewModel)
                }

                content()
            }
        }
    }
}


/**
 * unsaved dialog on back button
 */
@Composable
private fun UnsavedBackButtonDialog(
    onSave: (onComplete: () -> Unit) -> Unit,
    onDiscard: () -> Unit,
    onClose: () -> Unit
) {
    val navController = LocalMainNavController.current

    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = {
            onClose.invoke()
            onSave.invoke {
                navController.popBackStack()
            }
        },
        onDiscard = {
            onDiscard.invoke()
            navController.popBackStack()
            onClose.invoke()
        },
        dismissButtonText = MR.strings.discard
    )
}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun UnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    dismissButtonText: StringResource
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
                Text(dismissButtonText)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.discard
            )
        },
        title = { Text(MR.strings.unsavedChanges) },
        text = {
            Text(
                resource = MR.strings.unsavedChangesInformation,
                modifier = Modifier.testTag(TestTag.DialogUnsavedChanges)
            )
        }
    )

}

/**
 * bottom app bar
 * discard, save actions
 * fab for testing
 */
@Composable
private fun BottomAppBar(
    viewModel: IConfigurationViewModel,
) {
    val isHasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
    val isTestingEnabled by viewModel.isTestingEnabled.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = viewModel::discard,
                enabled = isHasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (isHasUnsavedChanges) Icons.Outlined.Delete else Icons.Filled.Delete,
                    contentDescription = MR.strings.discard,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = {
                    coroutineScope.launch {
                        viewModel.save()
                    }
                },
                enabled = isHasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (isHasUnsavedChanges) Icons.Outlined.Save else Icons.Filled.Save,
                    contentDescription = MR.strings.save
                )
            }
        },
        floatingActionButton = {
            val navController = LocalConfigurationNavController.current
            FloatingActionButton(
                modifier = Modifier
                    .testTag(TestTag.BottomAppBarTest)
                    .defaultMinSize(
                        minWidth = 56.0.dp,
                        minHeight = 56.0.dp,
                    ),
                onClick = {
                    viewModel.startTest()
                    navController.navigate(ConfigurationContentScreens.Test.route)
                },
                isEnabled = isTestingEnabled,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                contentColor = LocalContentColor.current,
                icon = {
                    Icon(
                        imageVector = if (isTestingEnabled) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                        contentDescription = MR.strings.test
                    )
                }
            )
        }
    )
}

/**
 * top app bar with title and back navigation button
 */
@Composable
private fun AppBar(title: StringResource, onBackClick: () -> Unit, icon: @Composable () -> Unit) {

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)
        ),
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = icon
            )
        }
    )
}