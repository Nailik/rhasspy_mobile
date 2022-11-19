package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.RadioButtonListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

/**
 *  list of porcupine languages
 */
@Composable
fun PorcupineLanguageScreen(viewModel: WakeWordConfigurationViewModel) {

    Scaffold(
        modifier = Modifier
            .testTag(TestTag.PorcupineLanguageScreen)
            .fillMaxSize(),
        topBar = { AppBar(MR.strings.language) }
    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {

            val selectedOption by viewModel.wakeWordPorcupineLanguage.collectAsState()

            val options = viewModel.porcupineLanguageOptions()

            Column {
                options.forEach { option ->

                    RadioButtonListItem(
                        modifier = Modifier.testTag(dataEnum = option),
                        text = option.text,
                        isChecked = selectedOption == option,
                        onClick = { viewModel.selectWakeWordPorcupineLanguage(option) }
                    )

                    CustomDivider()
                }
            }

        }

    }
}


/**
 * app bar for the language
 */
@Composable
private fun AppBar(title: StringResource) {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(title)
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