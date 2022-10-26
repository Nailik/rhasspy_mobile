package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

private enum class PorcupineKeywordScreens {
    Predefined,
    Custom
}

/**
 *  list of porcupine keyword option, contains option to add item from file manager
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorcupineKeywordScreen(viewModel: WakeWordConfigurationViewModel) {

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar() },
        bottomBar = {
            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                BottomNavBar()
            }
        }

    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {

            NavHost(
                navController = navController,
                startDestination = PorcupineKeywordScreens.Predefined.name
            ) {

                composable(PorcupineKeywordScreens.Predefined.name) {
                    PredefinedKeywords(viewModel)
                }

                composable(PorcupineKeywordScreens.Custom.name) {
                    CustomKeywords(viewModel)
                }

            }
        }
    }
}

//TODO use expected/actual enum with porcupine library
@Composable
private fun PredefinedKeywords(viewModel: WakeWordConfigurationViewModel) {
    val selectedOption = viewModel.wakeWordPorcupineKeywordOptions.collectAsState().value
        .elementAt(viewModel.wakeWordPorcupineKeywordOption.collectAsState().value)

    val options by viewModel.wakeWordPorcupineKeywordOptions.collectAsState()

    var selectedOptions by remember { mutableStateOf(listOf<Int>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        items(
            count = options.size,
            itemContent = { index ->

                val element = options.elementAt(index)

                ListElement(
                    modifier = Modifier.clickable {
                        val newList = selectedOptions.toMutableList()
                        if (selectedOptions.contains(index)) {
                            newList.remove(index)
                        } else {
                            newList.add(index)
                        }
                        selectedOptions = newList
                    },
                    icon = {
                        Checkbox(
                            checked = selectedOptions.contains(index),
                            onCheckedChange = {
                                val newList = selectedOptions.toMutableList()
                                if (it) {
                                    newList.add(index)
                                } else {
                                    newList.remove(index)
                                }
                                selectedOptions = newList
                            })
                    },
                    text = {
                        Text(element.lowercase().replaceFirstChar { it.uppercaseChar() })
                    },
                )

                if (selectedOptions.contains(index)) {
                    //sensitivity of porcupine
                    SliderListItem(
                        text = MR.strings.sensitivity,
                        value = viewModel.wakeWordPorcupineSensitivity.collectAsState().value,
                        onValueChange = viewModel::updateWakeWordPorcupineSensitivity
                    )
                }

                CustomDivider()
            }
/*
                        CheckBoxListItem(
                            text = element.lowercase().replaceFirstChar { it.uppercaseChar() },
                            isChecked = element == selectedOption,
                            onClick = { viewModel.selectWakeWordPorcupineKeywordOption(index) },
                            trailing = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(Icons.Filled.Tune, MR.strings.settings)
                                }
                            })

                        CustomDivider()
                    })*/
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomKeywords(viewModel: WakeWordConfigurationViewModel) {


}

/**
 * app bar for the keyword
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar() {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(MR.strings.wakeWord)
        },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
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

@Composable
private fun BottomNavBar() {

    NavigationBar {
        NavigationItem(screen = PorcupineKeywordScreens.Predefined,
            icon = { Icon(Icons.Filled.Mic, MR.strings.home) },
            label = { Text(MR.strings.home) })

        NavigationItem(screen = PorcupineKeywordScreens.Custom,
            icon = { Icon(Icons.Filled.Mic, MR.strings.home) },
            label = { Text(MR.strings.home) })
    }
}