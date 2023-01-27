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
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.logic.nativeutils.OverlayPermission
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests Overlay Permission redirecting and recognition
 */
@RunWith(AndroidJUnit4::class)
class OverlayPermissionTest {

    // activity necessary for permission
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"

    private val btnRequestPermission = "btnRequestPermission"
    private var permissionResult = false

    @NoLiveLiterals
    @Before
    fun setUp() {
        //set content
        composeTestRule.activity.setContent {
            AppTheme {

                RequiresOverlayPermission({ permissionResult = true }) { onClick ->
                    Button(onClick = { onClick.invoke(Unit) }) {
                        Text(btnRequestPermission)
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
    fun testAllow() = runBlocking {
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
            assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
            UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName))
            device.findObject(UiSelector().text(MR.strings.appName)).click()
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