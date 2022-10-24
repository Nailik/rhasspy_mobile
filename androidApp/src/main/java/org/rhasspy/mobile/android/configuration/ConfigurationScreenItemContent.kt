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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text

//unsaved
//save
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenItemContent(
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

    BackHandler {
        if (hasUnsavedChangesValue) {
            showDialog = true
        } else {
            navigation.popBackStack()
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { /*TODO*/ },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navigation.popBackStack()
                }) {
                    Text(MR.strings.ok)
                }
            }
        )
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(title) },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = onDiscard, enabled = hasUnsavedChangesValue) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = MR.strings.audioOutputURL,
                        )
                    }
                    IconButton(onClick = onSave, enabled = hasUnsavedChangesValue) {
                        Icon(Icons.Filled.Save, contentDescription = MR.strings.audioOutputURL)
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onTest,
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.PlayArrow, MR.strings.audioOutputURL)
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: StringResource) {
    /*
        val title = when (configurationScreen) {
            ConfigurationScreens.AudioPlayingConfiguration -> MR.strings.audioPlaying
            ConfigurationScreens.AudioRecordingConfiguration -> MR.strings.audioRecording
            ConfigurationScreens.DialogManagementConfiguration -> MR.strings.dialogueManagement
            ConfigurationScreens.IntentHandlingConfiguration -> MR.strings.intentHandling
            ConfigurationScreens.IntentRecognitionConfiguration -> MR.strings.intentRecognition
            ConfigurationScreens.MqttConfiguration -> MR.strings.mqtt
            ConfigurationScreens.RemoteHermesHttpConfiguration -> MR.strings.remoteHermesHTTP
            ConfigurationScreens.SpeechToTextConfiguration -> MR.strings.speechToText
            ConfigurationScreens.TextToSpeechConfiguration -> MR.strings.textToSpeech
            ConfigurationScreens.WakeWordConfiguration -> MR.strings.wakeWord
            ConfigurationScreens.WebServerConfiguration -> MR.strings.webserver
        }
    */
    val navigation = LocalMainNavController.current

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = navigation::popBackStack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.backup,
                )
            }
        })
}
