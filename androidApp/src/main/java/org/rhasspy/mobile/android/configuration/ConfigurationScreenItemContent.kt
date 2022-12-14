package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.FloatingActionButton
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.android.main.LocalConfigurationNavController
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.SetSystemColor
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.viewModels.configuration.IConfigurationViewModel

enum class ConfigurationContentScreens {
    Edit,
    Test
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
    content: LazyListScope.(onNavigate: (route: String) -> Unit) -> Unit
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == ConfigurationContentScreens.Edit.name) {
                viewModel.stopTest()
            }
        }
    }

    CompositionLocalProvider(
        LocalConfigurationNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = ConfigurationContentScreens.Edit.name,
            modifier = modifier
        ) {
            composable(ConfigurationContentScreens.Edit.name) {
                EditConfigurationScreen(
                    title = title,
                    viewModel = viewModel,
                    content = content
                )
            }
            composable(ConfigurationContentScreens.Test.name) {
                ConfigurationScreenTest(
                    viewModel = viewModel,
                    content = testContent,
                    onOpenPage = viewModel::onOpenTestPage
                )
            }
        }
    }
}

@Composable
private fun EditConfigurationScreen(
    title: StringResource,
    viewModel: IConfigurationViewModel,
    content: LazyListScope.(onNavigate: (route: String) -> Unit) -> Unit
) {
    SetSystemColor(0.dp)

    val navController = LocalMainNavController.current
    var showBackButtonDialog by rememberSaveable { mutableStateOf(false) }
    var showNavigateDialog by rememberSaveable { mutableStateOf(false) }
    var navigationRoute by rememberSaveable { mutableStateOf("") }

    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()

    fun onBackPress() {
        if (hasUnsavedChanges) {
            showBackButtonDialog = true
        } else {
            navController.popBackStack()
        }
    }

    fun onNavigate(route: String) {
        if (hasUnsavedChanges) {
            navigationRoute = route
            showNavigateDialog = true
        } else {
            navController.navigateSingle(route)
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

    //Show unsaved changes dialog navigate
    if (showNavigateDialog) {
        UnsavedNavigationButtonDialog(
            route = navigationRoute,
            onSave = viewModel::save,
            onClose = {
                showNavigateDialog = false
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
                        ServiceState(viewModel.serviceState.collectAsState().value)
                    }

                    content { route -> onNavigate(route) }
                }
            }
        }
}

private fun NavController.navigateSingle(route: String) {
    if (this.backQueue.lastOrNull { entry -> entry.destination.route == route } != null) {
        this.popBackStack(
            route = route,
            inclusive = false
        )
    } else {
        this.navigate(route)
    }
}


@Composable
private fun UnsavedBackButtonDialog(onSave: () -> Unit, onDiscard: () -> Unit, onClose: () -> Unit) {
    val navController = LocalMainNavController.current

    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = {
            onSave.invoke()
            navController.popBackStack()
            onClose.invoke()
        },
        onDiscard = {
            onDiscard.invoke()
            navController.popBackStack()
            onClose.invoke()
        },
        dismissButtonText = MR.strings.discard
    )
}

@Composable
private fun UnsavedNavigationButtonDialog(route: String, onSave: () -> Unit, onClose: () -> Unit) {
    val navController = LocalMainNavController.current

    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = {
            onSave.invoke()
            navController.navigateSingle(route)
            onClose.invoke()
        },
        onDiscard = onClose,
        dismissButtonText = MR.strings.cancel
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
    viewModel: IConfigurationViewModel,
) {
    val isHasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
    val isTestingEnabled by viewModel.isTestingEnabled.collectAsState()

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
                onClick = viewModel::save,
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
                    navController.navigate(ConfigurationContentScreens.Test.name)
                },
                isEnabled = isTestingEnabled,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                contentColor = LocalContentColor.current,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
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
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)),
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

@Composable
private fun ServiceState(serviceState: EventState) {

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        EventStateCard(
            eventState = serviceState,
            onClick = null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EventStateIcon(serviceState)
                ServiceStateText(serviceState)
            }
        }
    }

}

@Composable
private fun ServiceStateText(serviceState: EventState) {

    Text(
        resource = when (serviceState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
            is EventState.Disabled -> MR.strings.disabled
        }
    )

}