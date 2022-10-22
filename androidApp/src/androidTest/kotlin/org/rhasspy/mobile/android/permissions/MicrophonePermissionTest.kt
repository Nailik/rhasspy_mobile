package org.rhasspy.mobile.android.permissions

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.textMatches
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.android.ui.LocalSnackbarHostState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Tests Permission requesting
 *
 * differences between api levels:
 * - 21 : allow always on
 * - 23 : allow, deny, deny always (checkbox)
 * - 29 : allow, deny, deny always (button)
 * - 30 : allow only while using the app, ask every time, don't allow
 *
 * allow
 * allow only while using the app
 * ask every time
 *
 * deny
 * deny always
 *
 *
 * deny always redirect
 *
 * don't allow
 * info dialog
 * redirect allow
 *
 */

@RunWith(AndroidJUnit4::class)
class MicrophonePermissionTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()   // activity necessary for permission

    //.google is on devices with google play services and is missing on devices without play services
    private val permissionDialogPackageNameRegex = when {
        Build.VERSION.SDK_INT >= 29 -> "com(.google)?.android.permissioncontroller"
        else -> "com(.google)?.android.packageinstaller"
    }

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val appName = "Rhasspy Mobile"

    private val btnPermissionAllowForegroundOnly = "com.android.permissioncontroller:id/permission_allow_foreground_only_button"
    private val btnPermissionAllowOneTime = "com.android.permissioncontroller:id/permission_allow_one_time_button"
    private val radioBtnAllow = "com.android.permissioncontroller:id/allow_radio_button"
    private val radioBtnAllowForegroundOnly = "com.android.permissioncontroller:id/allow_foreground_only_radio_button"
    private val radioBtnAsk = "com.android.permissioncontroller:id/ask_radio_button"
    private val txtEntityHeader = "com.android.settings:id/entity_header_title"

    private val cbhDoNotAsk = "com.android.packageinstaller:id/do_not_ask_checkbox"
    private val btnDoNotAskAgain = "com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button"

    private val allowPermissionRegex = ".*\\/permission_allow_button"
    private val denyPermissionRegex = ".*\\/permission_deny_button"

    private val btnRequestPermission = "btnRequestPermission"

    private var permissionResult = false

    @Before
    fun setUp() {
        //set content
        composeTestRule.activity.setContent {
            Content()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @NoLiveLiterals
    @Composable
    private fun Content() {

        val snackbarHostState = remember { SnackbarHostState() }

        CompositionLocalProvider(
            //snack bar necessary
            LocalSnackbarHostState provides snackbarHostState
        ) {

            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState, modifier = Modifier.testTag("test)")) },
                ) {

                    RequiresMicrophonePermission(MR.strings.defaultText, { permissionResult = true }) { onClick ->
                        Button(onClick = onClick) {
                            Text("requestPermission")
                        }
                    }
                }

            }
        }
    }

    /**
     * User clicks button
     * System dialog is shown
     * User selects allow
     * Dialog is closed and permission granted
     */
    @Test
    fun testAllow() = runBlocking {
        if (Build.VERSION.SDK_INT < 23) {
            //permission always granted
            assertTrue { MicrophonePermission.granted.value }
            return@runBlocking
        }

        if (Build.VERSION.SDK_INT >= 30) {
            //no simple allow button on api 30
            return@runBlocking
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }

        //User selects allow
        device.findObject(UiSelector().resourceIdMatches(allowPermissionRegex)).click()

        //Dialog is closed and permission granted
        composeTestRule.awaitIdle()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        assertTrue { MicrophonePermission.granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * User selects allow while using the app
     * Dialog is closed and permission granted
     */
    @Test
    fun testAllowWhileUsing() = runBlocking {
        if (Build.VERSION.SDK_INT < 23) {
            //permission always granted
            assertTrue { MicrophonePermission.granted.value }
            return@runBlocking
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }

        //User selects allow while using the app
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowForegroundOnly)).click()

        //Dialog is closed and permission granted
        getInstrumentation().waitForIdleSync()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        assertTrue { permissionResult }
        assertTrue { MicrophonePermission.granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * user selects only once
     * Dialog is closed and permission granted
     */
    @Test
    fun testAllowOnce() = runBlocking {
        if (Build.VERSION.SDK_INT < 30) {
            //allow one time only available above and on api 30
            return@runBlocking
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }

        //user selects only once
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowOneTime)).click()

        //Dialog is closed and permission granted
        composeTestRule.awaitIdle()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        assertTrue { MicrophonePermission.granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * user selects deny always
     * Dialog is closed and permission granted
     */
    @Test
    fun testDenyAlways() = runBlocking {
        if (Build.VERSION.SDK_INT >= 30 || Build.VERSION.SDK_INT < 23) {
            //no always allow button above api 30
            //no permission request necessary pre 23
            return@runBlocking
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        //deny permission
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).clickAndWaitForNewWindow()

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //deny always
        if (Build.VERSION.SDK_INT < 29) {
            //check do not ask again
            device.findObject(UiSelector().resourceId(cbhDoNotAsk)).clickAndWaitForNewWindow()
            //deny always
            device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).clickAndWaitForNewWindow()
        } else {
            //directly click do not ask again
            device.findObject(UiSelector().resourceId(btnDoNotAskAgain)).clickAndWaitForNewWindow()
        }

        //user selects only once
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowOneTime)).click()

        //Dialog is closed and permission not granted
        composeTestRule.awaitIdle()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //snack bar shown
        assertTrue { device.findObject(UiSelector().textMatches(MR.strings.microphonePermissionDenied)).exists() }
    }

    /**
     * User clicks button
     * System dialog is shown
     * user selects deny/ don't allow
     * Dialog is closed and permission not granted
     * Snackbar is shown
     *
     * User clicks button
     * InformationDialog is shown
     * Cancel clicked
     * Dialog closed
     *
     * User clicks button
     * InformationDialog is shown
     * ok clicked
     * Dialog closed
     * System Dialog is shown
     * user selects deny/ don't allow
     *
     * User clicks button
     * Snackbar is shown
     * Snackbar action is clicked
     * User is redirected to settings
     * User allows permission in settings
     * User clicks back
     * Permission is allowed
     */
    @Test
    fun testInformationDialog() = runBlocking {
        if (Build.VERSION.SDK_INT < 23) {
            //permission always granted
            assertTrue { MicrophonePermission.granted.value }
            return@runBlocking
        }
        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }


        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        //user selects deny
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()
        //Dialog is closed and permission not granted
        composeTestRule.awaitIdle()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        assertFalse { MicrophonePermission.granted.value }
        //Snackbar is shown
        assertTrue { device.findObject(UiSelector().textMatches(MR.strings.microphonePermissionDenied)).exists() }
        assertFalse { MicrophonePermission.granted.value }


        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        composeTestRule.awaitIdle()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission).assertExists()
        //Cancel clicked
        composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
        //Dialog closed
        composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission).assertDoesNotExist()

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        composeTestRule.awaitIdle()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission).assertExists()
        //ok clicked
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closed
        composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission).assertDoesNotExist()
        //System Dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        //user selects deny
        if(Build.VERSION.SDK_INT < 29) {
            device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()
        } else {
            device.findObject(UiSelector().resourceIdMatches(btnDoNotAskAgain)).click()
        }

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //Snackbar is shown
        assertTrue { device.findObject(UiSelector().textMatches(MR.strings.microphonePermissionDenied)).exists() }
        //Snackbar action is clicked
        composeTestRule.onNodeWithText("Einstellungen").performClick()
        //User is redirected to settings
        getInstrumentation().waitForIdleSync()
        device.findObject(UiSelector().resourceId(txtEntityHeader).text(appName)).exists()
        device.findObject(UiSelector().packageName("com.android.settings")).clickAndWaitForNewWindow()
        //User allows permission in settings
        device.findObject(UiSelector().textMatches("Mikrofon")).clickAndWaitForNewWindow() //TODO language

        if (Build.VERSION.SDK_INT > 28) {
            //pre api 28 there is no further navigation necessary because its a toggle
            device.findObject(UiSelector().resourceId(radioBtnAllowForegroundOnly)).click()
        }
        //User clicks back
        device.pressBack()
        device.pressBack()
        device.pressBack()

        //Permission is allowed
        getInstrumentation().waitForIdleSync()
        assertTrue { MicrophonePermission.granted.value }
    }
}
