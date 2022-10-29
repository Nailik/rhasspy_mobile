package org.rhasspy.mobile.android.configuration

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.assertTextEquals
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Content Test of Configuration screens
 * save, discard and test buttons
 */
class ConfigurationScreenItemContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val configurationScreenItemContentNavigation = "ConfigurationScreenItemContent"
    private val startNavigation = "start"

    private val hasUnsavedChanges = MutableStateFlow(false)
    private var onSave = false
    private var onTest = false
    private var onDiscard = false

    private val btnStartTest = "btnStartTest"
    private val toolbarTitle = MR.strings.defaultText

    @Before
    fun setUp() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startNavigation,
                ) {

                    composable(startNavigation) {
                        //button to open config screen in order to test back press
                        Button(onClick = { navController.navigate(configurationScreenItemContentNavigation) }) {
                            Text(btnStartTest)
                        }
                    }

                    composable(configurationScreenItemContentNavigation) {

                        ConfigurationScreenItemContent(
                            modifier = Modifier,
                            title = toolbarTitle,
                            hasUnsavedChanges = hasUnsavedChanges,
                            onSave = { onSave = true },
                            onTest = { onTest = true },
                            onDiscard = { onDiscard = true }
                        ) {

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
    fun testUnsavedChanges() {
        //open screen
        composeTestRule.onNodeWithText(btnStartTest).performClick()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertExists()

        //hasUnsavedChanges false
        hasUnsavedChanges.value = false
        //save and discard disabled
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsNotEnabled()

        //hasUnsavedChanges true
        hasUnsavedChanges.value = true
        //save and discard enabled
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled()

        //discard click invokes discard
        assertFalse { onDiscard }
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarDiscard).performClick()
        assertTrue { onDiscard }
        //save click invokes save
        assertFalse { onSave }
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).performClick()
        assertTrue { onSave }

        //app bar back click shows dialog
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertExists()
        //outside click closes dialog
        device.click(100, 100)
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertDoesNotExist()

        //back click shows dialog
        device.pressBack()
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertExists()
        onDiscard = false
        //discard click invokes discard and navigate back
        assertFalse { onDiscard }
        composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
        assertTrue { onDiscard }
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertDoesNotExist()


        //open screen
        composeTestRule.onNodeWithText(btnStartTest).performClick()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertExists()
        hasUnsavedChanges.value = true

        //back click shows dialog
        device.pressBack()
        composeTestRule.onNodeWithTag(TestTag.DialogUnsavedChanges).assertExists()
        onSave = false
        //save click invokes save and navigate back
        assertFalse { onSave }
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        assertTrue { onSave }
        composeTestRule.onNodeWithTag(TestTag.ConfigurationScreenItemContent).assertDoesNotExist()
    }

}