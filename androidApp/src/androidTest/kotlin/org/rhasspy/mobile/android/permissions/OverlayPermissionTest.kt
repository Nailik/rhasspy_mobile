package org.rhasspy.mobile.android.permissions

import android.os.Build
import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.theme.AppTheme
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests Overlay Permission redirecting and recognition
 */
@RunWith(AndroidJUnit4::class)
class OverlayPermissionTest : FlakyTest() {

    // activity necessary for permission
    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"

    private val btnRequestPermission = "btnRequestPermission"
    private var permissionResult = false

    @NoLiveLiterals
    @Before
    fun setUp() {
        val testViewModel = TestViewModel()
        //set content
        composeTestRule.activity.setContent {
            AppTheme {
                TestContentProvider {
                    Screen(testViewModel) {
                        Button(onClick = testViewModel::onRequestOverlayPermission) {
                            Text(btnRequestPermission)
                        }
                    }
                }
            }
        }
    }


    /**
     * User clicks button
     * InformationDialog is shown
     * Cancel clicked
     * Dialog closed
     *
     * User clicks button
     * InformationDialog is shown
     * Ok clicked
     *
     * Redirected to settings
     * User allows permission
     * clicks back
     * permission is granted
     * invoke was done
     */
    @Test
    fun testAllow() = runTest {
        device.resetOverlayPermission(composeTestRule.activity)

        permissionResult = false
        assertFalse { OverlayPermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogInformationOverlayPermission).assertExists()
        //Cancel clicked
        composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
        //Dialog closed
        composeTestRule.onNodeWithTag(TestTag.DialogInformationOverlayPermission)
            .assertDoesNotExist()

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogInformationOverlayPermission).assertExists()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //Ok clicked
            composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
            //on Q app is restarted when allowing overlay permission

            //Redirected to settings
            getInstrumentation().waitForIdleSync()
            device.wait(Until.hasObject(By.pkg(settingsPage.toPattern())), 5000)
            assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
            UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName.stable))
            device.findObject(UiSelector().text(MR.strings.appName.stable)).click()
            device.findObject(UiSelector().className(Switch::class.java)).click()
            //User clicks back
            device.pressBack()
            device.pressBack()

            //app will be closed when pressing one more back
            OverlayPermission.update()
            //Dialog is closed and permission granted
            assertTrue { OverlayPermission.granted.value }
        }

        assertTrue { true }
    }
}