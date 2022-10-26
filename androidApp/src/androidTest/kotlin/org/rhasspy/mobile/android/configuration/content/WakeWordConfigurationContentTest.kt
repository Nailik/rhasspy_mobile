package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

class WakeWordConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                WakeWordConfigurationContent(viewModel)
            }
        }

    }

    /**
     * option is disable
     * porcupine options not visible
     *
     * user clicks porcupine
     * new option is set
     * porcupine options visible
     *
     * user clicks save
     * new option is saved
     */
    @Test
    fun testWakeWordContent() = runBlocking {

    }

    /**
     * option is porcupine
     *
     * user changes access key
     * access key is change
     *
     * user clicks generate AccessKey
     * browser is opened
     *
     * user changes sensitivity
     * sensitivity is changed
     *
     * user clicks save
     * access key is saved
     * sensitivity is saved
     */
    @Test
    fun testPorcupineOptions() = runBlocking {

    }

    /**
     * option is porcupine
     * Ok_google is saved
     *
     * user clicks wake word
     * Ok_google is selected
     *
     * User clicks Alexa
     * Alexa is selected
     *
     * User clicks select file
     * Browser is opened
     * User selects file
     *
     * user clicks back twice
     * file is added as option
     * new file is selected
     *
     * user clicks back
     * user clicks save
     * wake word option is saved
     */
    @Test
    fun testPorcupineWakeWordOptions() = runBlocking {

    }

    /**
     * option is porcupine
     * English is saved
     *
     * user clicks language
     * english is selected
     *
     * user clicks german
     * german is selected
     *
     * user clicks back
     * user click save
     * german is saved
     */
    @Test
    fun testPorcupineLanguageOptions() = runBlocking {

    }
}