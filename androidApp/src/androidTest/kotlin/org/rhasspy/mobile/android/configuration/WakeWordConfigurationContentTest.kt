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
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.Change.SelectWakeDomainOption
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeDomainPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WakeWordConfigurationContentTest : FlakyTest() {

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val mainViewModel = get<MainScreenViewModel>()
    private val viewModel = get<WakeDomainConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        MainScreen(mainViewModel)
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
        viewModel.onEvent(SelectWakeDomainOption(WakeDomainOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeDomainOption.Disabled, viewModel.viewState.value.editData.wakeDomainOption)

        //porcupine options not visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertDoesNotExist()
        //user clicks porcupine
        composeTestRule.onNodeWithTag(WakeDomainOption.Porcupine).performClick()
        //porcupine options visible
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertIsDisplayed()

        //user clicks save
        composeTestRule.saveBottomAppBar()
        WakeDomainConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //new option is saved
            assertEquals(WakeDomainOption.Porcupine, it.wakeDomainOption)
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
        viewModel.onEvent(SelectWakeDomainOption(WakeDomainOption.Porcupine))
        viewModel.onEvent(UpdateWakeDomainPorcupineAccessToken(""))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeDomainOption.Porcupine, viewModel.viewState.value.editData.wakeDomainOption)

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
        viewModel.onEvent(SelectWakeDomainOption(WakeDomainOption.Porcupine))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeDomainOption.Porcupine, viewModel.viewState.value.editData.wakeDomainOption)
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