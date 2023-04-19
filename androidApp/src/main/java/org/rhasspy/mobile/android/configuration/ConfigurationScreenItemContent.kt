package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.UiEventEffect
import org.rhasspy.mobile.android.content.ServiceStateHeader
import org.rhasspy.mobile.android.content.elements.FloatingActionButton
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.SetSystemColor
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.ui.event.StateEvent.Consumed
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiNavigate
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiNavigate.PopBackStack

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
fun <V : IConfigurationEditViewState> ConfigurationScreenItemContent(
    viewState: ConfigurationViewState<V>,
    onAction: (IConfigurationUiEvent) -> Unit,
    onConsumed: (IConfigurationUiNavigate) -> Unit,
    modifier: Modifier,
    config: ConfigurationScreenConfig = ConfigurationScreenConfig(MR.strings.save.stable),
    testContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == ConfigurationContentScreens.Edit.route) {
                onAction(StopTest)
            }
        }
    }

    BackHandler(viewState.isBackPressDisabled) {}

    if (viewState.isLoading) {
        Surface {
            val navControllerMain = LocalMainNavController.current
            UiEventEffect(
                event = viewState.popBackStack,
                onConsumed = onConsumed
            ) {
                navControllerMain.popBackStack()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    } else {
        Column {
            val serviceStateHeaderViewState by viewState.serviceViewState.collectAsState()
            ServiceStateHeader(serviceStateHeaderViewState)

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
                            title = config.title,
                            viewState = viewState.editViewState.collectAsState().value,
                            showUnsavedChangesDialog = viewState.showUnsavedChangesDialog,
                            popBackStack = viewState.popBackStack,
                            onAction = onAction,
                            onConsumed = onConsumed,
                            content = content
                        )
                    }
                    composable(ConfigurationContentScreens.Test.route) {
                        ConfigurationScreenTest(
                            viewState = viewState.testViewState.collectAsState().value,
                            onAction = onAction,
                            content = testContent
                        )
                    }
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
    title: StableStringResource,
    viewState: IConfigurationEditViewState,
    showUnsavedChangesDialog: Boolean,
    popBackStack: PopBackStack = PopBackStack(Consumed),
    onAction: (IConfigurationUiEvent) -> Unit,
    onConsumed: (IConfigurationUiNavigate) -> Unit,
    content: LazyListScope.() -> Unit
) {
    SetSystemColor(0.dp)

    val navController = LocalMainNavController.current
    UiEventEffect(
        event = popBackStack,
        onConsumed = onConsumed
    ) {
        navController.popBackStack()
    }


    //Back handler to show dialog if there are unsaved changes
    BackHandler(onBack = { (onAction(BackPress)) })

    //Show unsaved changes dialog back press
    if (showUnsavedChangesDialog) {
        UnsavedBackButtonDialog(
            onSave = { onAction(Save) },
            onDiscard = { onAction(Discard) },
            onClose = { onAction(DismissDialog) }
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                onBackClick = { (onAction(BackPress)) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                hasUnsavedChanges = viewState.hasUnsavedChanges,
                isTestingEnabled = viewState.isTestingEnabled,
                onAction = { onAction(it) },
            )
        }
    ) { paddingValues ->
        Surface(tonalElevation = 1.dp) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
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
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onClose: () -> Unit
) {
    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = { onSave.invoke() },
        onDiscard = { onDiscard.invoke() },
        dismissButtonText = MR.strings.discard.stable
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
    dismissButtonText: StableStringResource
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onSave,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.save.stable)
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
                contentDescription = MR.strings.discard.stable
            )
        },
        title = { Text(MR.strings.unsavedChanges.stable) },
        text = {
            Text(
                resource = MR.strings.unsavedChangesInformation.stable,
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
    hasUnsavedChanges: Boolean,
    isTestingEnabled: Boolean,
    onAction: (IConfigurationUiEvent) -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = { onAction(Discard) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Delete else Icons.Filled.Delete,
                    contentDescription = MR.strings.discard.stable,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = { onAction(Save) },
                enabled = hasUnsavedChanges
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Outlined.Save else Icons.Filled.Save,
                    contentDescription = MR.strings.save.stable
                )
            }
        },
        floatingActionButton = {
            FloatingActionButtonElement(
                isTestingEnabled = isTestingEnabled,
                onAction = { onAction(it) }
            )
        }
    )
}

@Composable
private fun FloatingActionButtonElement(
    isTestingEnabled: Boolean,
    onAction: (IConfigurationUiEvent) -> Unit
) {
    val navController = rememberNavController()
    FloatingActionButton(
        modifier = Modifier
            .testTag(TestTag.BottomAppBarTest)
            .defaultMinSize(
                minWidth = 56.0.dp,
                minHeight = 56.0.dp,
            ),
        onClick = {
            onAction(StartTest)
            navController.navigate(ConfigurationContentScreens.Test.route)
        },
        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
        contentColor = LocalContentColor.current,
        isEnabled = isTestingEnabled,
        icon = {
            Icon(
                imageVector = if (isTestingEnabled) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                contentDescription = MR.strings.test.stable
            )
        }
    )
}

/**
 * top app bar with title and back navigation button
 */
@Composable
private fun AppBar(title: StableStringResource, onBackClick: () -> Unit, icon: @Composable () -> Unit) {

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
                onClick = { onBackClick() },
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = icon
            )
        }
    )
}