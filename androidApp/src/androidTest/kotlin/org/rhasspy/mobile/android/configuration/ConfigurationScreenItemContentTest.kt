package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.ui.TestTag

/**
 * Content Test of Configuration screens
 * save, discard and test buttons
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConfigurationScreenItemContentTest : FlakyTest() {

    @get:Rule(order = 0)
    val composeTestRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val configurationScreenItemContentNavigation = "ConfigurationScreenItemContent"
    private val startNavigation = "start"

    private val viewModel = TestViewModel()

    private val btnStartTest = "btnStartTest"
    private val toolbarTitle = MR.strings.defaultText.stable

    @Before
    fun before() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    private fun setupUi() {
        scenario.onActivity { activity ->
            activity.setContent {
                val navController = rememberNavController()

                TestContentProvider(
                    navController = navController,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startNavigation,
                    ) {

                        composable(startNavigation) {
                            //button to open config screen in order to test back press
                            Button(
                                onClick = {
                                    navController.navigate(configurationScreenItemContentNavigation)
                                }
                            ) {
                                Text(btnStartTest)
                            }
                        }

                        composable(configurationScreenItemContentNavigation) {

                            ConfigurationScreenItemContent(
                                modifier = Modifier,
                                config = ConfigurationScreenConfig(toolbarTitle),
                                viewState = viewModel.viewState.collectAsState().value,
                                onAction = { viewModel.onAction(it) },
                                onConsumed = { viewModel.onConsumed(it) },
                                testContent = { },
                                content = { }
                            )

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
    fun testContent() {
        setupUi()
        //open screen
        composeTestRule.onNodeWithText(btnStartTest).performClick()
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
    @AllowFlaky(attempts = 5)
    fun testUnsavedChanges() = runTest {
        setupUi()
        //open screen
        composeTestRule.onNodeWithText(btnStartTest).performClick()
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

        viewModel.setUnsavedChanges(true)
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).performClick()
        composeTestRule.awaitSaved(viewModel)

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
        composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertDoesNotExist()


        //open screen
        composeTestRule.onNodeWithText(btnStartTest).performClick()
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