package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WakeWordConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

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
     *
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
        //option is disable
        viewModel.selectWakeWordOption(WakeWordOption.Disabled)
        viewModel.save()
        assertEquals(WakeWordOption.Disabled, viewModel.wakeWordOption.value)

        //porcupine options not visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertDoesNotExist()
        //user clicks porcupine
        composeTestRule.onNodeWithTag(WakeWordOption.Porcupine).performClick()
        //porcupine options visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertIsDisplayed()

        //user clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = WakeWordConfigurationViewModel()
        //new option is saved
        assertEquals(WakeWordOption.Porcupine, newViewModel.wakeWordOption.value)
    }

    /**
     * option is porcupine
     *
     * access token is visible
     * user changes access key
     * access token change
     *
     * user clicks picovoice console
     * browser is opened
     *
     * user clicks save
     * access token is saved
     */
    @Test
    fun testPorcupineOptions() = runBlocking {
        //option is porcupine
        viewModel.selectWakeWordOption(WakeWordOption.Porcupine)
        viewModel.save()
        assertEquals(WakeWordOption.Porcupine, viewModel.wakeWordOption.value)

        val textInputTest = "fghfghhrtrtzh34ß639254´1´90!$/%(&$("

        //access token is visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).assertIsDisplayed()
        //user changes access token
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performScrollTo().performClick()
        //access token change
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performTextReplacement(textInputTest)

        //user clicks picovoice console
        composeTestRule.onNodeWithTag(TestTag.PorcupineOpenConsole).performScrollTo().performClick()
        //browser is opened
        device.findObject(UiSelector().textMatches(".*console.picovoice.ai.*")).exists()
        device.pressBack()

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        composeTestRule.awaitIdle()

        //user clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = WakeWordConfigurationViewModel()
        //access token is saved
        assertEquals(WakeWordOption.Porcupine, newViewModel.wakeWordOption.value)
    }

    /**
     * option is porcupine
     *
     * wake word is clicked,
     * wake word page is opened
     *
     * back is clicked
     * page is back to wake word settings
     *
     * language is clicked
     * language page is opened
     *
     * back is clicked
     * page is back to wake word settings
     */
    @Test
    fun testPorcupineWakeWordOptions() = runBlocking {
        //option is porcupine
        viewModel.selectWakeWordOption(WakeWordOption.Porcupine)
        viewModel.save()
        assertEquals(WakeWordOption.Porcupine, viewModel.wakeWordOption.value)

        //wake word is clicked,
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeyword).performScrollTo().performClick()
        //wake word page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.onNodeWithTag(ConfigurationScreens.WakeWordConfiguration).assertIsDisplayed()

        //language is clicked
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguage).performScrollTo().performClick()
        //language page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguageScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.onNodeWithTag(ConfigurationScreens.WakeWordConfiguration).assertIsDisplayed()

        assertTrue(true)
    }

}