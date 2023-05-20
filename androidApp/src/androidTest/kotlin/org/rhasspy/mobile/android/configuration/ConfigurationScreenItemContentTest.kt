package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.ConfigurationScreenDestinationType

/**
 * Content Test of Configuration screens
 * save, discard and test buttons
 */
class ConfigurationScreenItemContentTest : FlakyTest() {

    @get:Rule(order = 0)
    val composeTestRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val viewModel = TestViewModel()

    private val toolbarTitle = MR.strings.defaultText.stable

    @Before
    fun before() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    private fun setupUi() {
        val navigator: Navigator = get()

        scenario.onActivity { activity ->
            activity.setContent {

                TestContentProvider {

                    val destination by navigator.navStack.collectAsState()

                    when (destination.lastOrNull()) {

                        MainScreenNavigationDestination.ConfigurationScreen -> {
                            Screen(viewModel) {
                                ConfigurationScreenItemContent(
                                    modifier = Modifier.testTag(TestTag.ConfigurationScreenItemContent),
                                    screenType = ConfigurationScreenDestinationType.Edit,
                                    config = ConfigurationScreenConfig(toolbarTitle),
                                    viewState = viewModel.viewState.collectAsState().value,
                                    onAction = { viewModel.onAction(it) },
                                    testContent = { },
                                    content = { }
                                )
                            }
                        }

                        else -> {
                            Button(modifier = Modifier.testTag(TestTag.OpenConfigScreen),
                                onClick = { navigator.navigate(MainScreenNavigationDestination.ConfigurationScreen) }) {
                                Text("ConfigurationScreen")
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * toolbar contains text
     * toolbar has back button
     * save, discard, fab exist
     */
    @Test
    fun testContent() = runTest {
        setupUi()
        composeTestRule.awaitIdle()
        //open screen
        composeTestRule.onNodeWithTag(TestTag.OpenConfigScreen).performClick()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarTest).performClick()
        //toolbar contains text
        composeTestRule.onNodeWithTag(TestTag.AppBarTitle).assertTextEquals(toolbarTitle)
        //toolbar has back button
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).assertExists()
        //save, discard, fab exist
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).assertExists()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertExists()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarTest).assertExists()
    }

    /**
     * user opens screen
     *
     * hasUnsavedChanges false
     * save and discard disabled
     *
     * hasUnsavedChanges true
     * save and discard enabled
     *
     * discard click invokes discard
     * save click invokes save
     *
     * app bar back click shows dialog
     * outside click closes dialog
     * back click shows dialog
     * discard click invokes discard and navigate back
     *
     * user opens screen
     * hasUnsavedChanges true
     * back click shows dialog
     * save click invokes save and navigate back
     */
    @Test
    fun testUnsavedChanges() = runTest {
        setupUi()
        composeTestRule.awaitIdle()
        //open screen
        composeTestRule.onNodeWithTag(TestTag.OpenConfigScreen).performClick()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarTest).performClick()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertExists()

        //hasUnsavedChanges false
        viewModel.setUnsavedChanges(false)
        //save and discard disabled
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsNotEnabled()

        //hasUnsavedChanges true
        viewModel.setUnsavedChanges(true)
        //save and discard enabled
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled()

        viewModel.onSave = false
        viewModel.onDiscard = false

        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).performClick()
        composeTestRule.awaitIdle()

        viewModel.setUnsavedChanges(true)
        composeTestRule.saveBottomAppBar(viewModel)

        viewModel.setUnsavedChanges(true)
        //app bar back click shows dialog
        composeTestRule.awaitIdle()
        device.pressBack()
        composeTestRule.awaitIdle()
        //outside click closes dialog
        device.click(300, 300)
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertDoesNotExist()

        //back click shows dialog
        composeTestRule.awaitIdle()
        device.pressBack()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertExists()
        viewModel.onDiscard = false
        //discard click invokes discard and navigate back
        composeTestRule.onNodeWithTag(TestTag.DialogCancel, true).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertDoesNotExist()

        //open screen
        composeTestRule.onNodeWithTag(TestTag.OpenConfigScreen).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertExists()
        viewModel.setUnsavedChanges(true)

        //back click shows dialog
        composeTestRule.awaitIdle()
        device.pressBack()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertExists()
        viewModel.onSave = false
        //save click invokes save and navigate back
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertDoesNotExist()
    }

}