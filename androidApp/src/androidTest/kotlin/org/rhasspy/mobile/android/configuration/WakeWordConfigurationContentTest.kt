package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTestNew
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WakeWordConfigurationContentTest : FlakyTestNew() {

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val viewModel = get<WakeWordConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        MainScreen(LocalViewModelFactory.current)
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
    @AllowFlaky
    fun testWakeWordContent() = runTest {
        get<INavigator>().navigate(WakeWordConfigurationScreen)
        setupContent()

        //option is disable
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Disabled, viewModel.viewState.value.editData.wakeWordOption)

        //porcupine options not visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertDoesNotExist()
        //user clicks porcupine
        composeTestRule.onNodeWithTag(WakeWordOption.Porcupine).performClick()
        //porcupine options visible
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertIsDisplayed()

        //user clicks save
        composeTestRule.saveBottomAppBar()
        WakeWordConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //new option is saved
            assertEquals(WakeWordOption.Porcupine, it.wakeWordOption)
        }
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
    @AllowFlaky
    fun testPorcupineOptions() = runTest {
        get<INavigator>().navigate(WakeWordConfigurationScreen)
        setupContent()

        //option is porcupine
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.onEvent(UpdateWakeWordPorcupineAccessToken(""))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Porcupine, viewModel.viewState.value.editData.wakeWordOption)

        val textInputTest = "fghfghhrtrtzh34ß639254´1´90!$/%(&$("

        //access token is visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).assertIsDisplayed()
        //user changes access token
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        //access token change
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performTextInput(textInputTest)

        //user clicks picovoice console
        composeTestRule.onNodeWithTag(TestTag.PorcupineOpenConsole).performScrollTo().performClick()
        //browser is opened
        device.wait(Until.hasObject(By.text(".*console.picovoice.ai.*".toPattern())), 5000)
        device.findObject(UiSelector().textMatches(".*console.picovoice.ai.*")).exists()
        device.pressBack()

        composeTestRule.awaitIdle()
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
    @AllowFlaky
    fun testPorcupineWakeWordOptions() = runTest {
        get<INavigator>().navigate(WakeWordConfigurationScreen)
        setupContent()

        //option is porcupine
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Porcupine, viewModel.viewState.value.editData.wakeWordOption)
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        //wake word is clicked,
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeyword).performScrollTo().performClick()
        //wake word page is opened
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        //language is clicked
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguage).performScrollTo().performClick()
        //language page is opened
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguageScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        assertTrue(true)
    }

}