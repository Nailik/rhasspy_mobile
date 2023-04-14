package org.rhasspy.mobile.android.configuration.content

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WakeWordConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val viewModel = WakeWordConfigurationViewModel(
        service = WakeWordService(),
        testRunner = WakeWordConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalSnackbarHostState provides snackbarHostState,
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
        viewModel.onAction(SelectWakeWordOption(WakeWordOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        assertEquals(WakeWordOption.Disabled, viewState.value.wakeWordOption)

        //porcupine options not visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertDoesNotExist()
        //user clicks porcupine
        composeTestRule.onNodeWithTag(WakeWordOption.Porcupine).performClick()
        //porcupine options visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertIsDisplayed()

        //user clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = WakeWordConfigurationViewState()
        //new option is saved
        assertEquals(WakeWordOption.Porcupine, newViewState.wakeWordOption)
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
        viewModel.onAction(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.onAction(UpdateWakeWordPorcupineAccessToken(""))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        assertEquals(WakeWordOption.Porcupine, viewState.value.wakeWordOption)

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
        composeTestRule.awaitSaved(viewModel)
        val newViewState = WakeWordConfigurationViewState()
        //access token is saved
        assertEquals(WakeWordOption.Porcupine, newViewState.wakeWordOption)
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
        viewModel.onAction(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        assertEquals(WakeWordOption.Porcupine, viewState.value.wakeWordOption)

        //wake word is clicked,
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeyword).performScrollTo().performClick()
        //wake word page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.onNodeWithTag(ConfigurationScreenType.WakeWordConfiguration)
            .assertIsDisplayed()

        //language is clicked
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguage).performScrollTo().performClick()
        //language page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguageScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.onNodeWithTag(ConfigurationScreenType.WakeWordConfiguration)
            .assertIsDisplayed()

        assertTrue(true)
    }

}