package org.rhasspy.mobile.android.permissions

import android.Manifest
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
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.revokePermission
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.android.ui.LocalSnackbarHostState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import kotlin.test.assertFalse
import kotlin.test.assertTrue


// To indicate that we've to run it with AndroidJUnit runner
class MicrophonePermission {

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
        //clear permissions
        revokePermission(Manifest.permission.RECORD_AUDIO)

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
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) {

                    val requestMicrophonePermission = requestMicrophonePermission(MR.strings.defaultText) {
                        permissionResult = it
                    }

                    Button(onClick = requestMicrophonePermission::invoke) {
                        Text("requestPermission")
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
        if (Build.VERSION.SDK_INT >= 30 || Build.VERSION.SDK_INT < 23) {
            //no simple allow button on api 30
            //pre api 23 there are no permission to request at all
            return@runBlocking
        }

        permissionResult = false
        revokePermission(Manifest.permission.RECORD_AUDIO)

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
        permissionResult = false
        revokePermission(Manifest.permission.RECORD_AUDIO)

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }

        //User selects allow while using the app
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowForegroundOnly)).click()

        //Dialog is closed and permission granted
        composeTestRule.awaitIdle()
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
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
        permissionResult = false
        revokePermission(Manifest.permission.RECORD_AUDIO)

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
     * user selects deny
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
     * user selects deny
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
    fun testInformationDialog() = runBlocking{
        permissionResult = false
        revokePermission(Manifest.permission.RECORD_AUDIO)

        println("test01")
        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()

        println("test10")
        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }

        println("test0")
        //user selects deny
        getInstrumentation().waitForIdleSync()
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()
        getInstrumentation().waitForIdleSync()

        println("test100")
        //Dialog is closed and permission not granted
        getInstrumentation().waitForIdleSync()
        println("test1")
        assertFalse { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        println("test2")
        assertFalse { MicrophonePermission.granted.value }
        println("test3")
        getInstrumentation().waitForIdleSync()
        println("test4")

        //Snackbar is shown
        composeTestRule.onNodeWithText("requestPermission").performClick()
        println("test52")
        composeTestRule.awaitIdle()
        println("test5")
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDenied).toString(composeTestRule.activity)).assertExists()
        println("test6")

        /*
        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //InformationDialog is shown
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDialogTitle).toString(composeTestRule.activity)).assertExists()
        //Cancel clicked
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.cancel).toString(composeTestRule.activity)).assertExists()
        //Dialog closed
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDialogTitle).toString(composeTestRule.activity)).assertDoesNotExist()

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //InformationDialog is shown
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDialogTitle).toString(composeTestRule.activity)).assertExists()
        //ok clicked
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.ok).toString(composeTestRule.activity)).assertExists()
        //Dialog closed
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDialogTitle).toString(composeTestRule.activity)).assertDoesNotExist()
        //System Dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue { device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex)).exists() }
        //user selects deny
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()

        //User clicks button
        composeTestRule.onNodeWithText("requestPermission").performClick()
        //Snackbar is shown
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.microphonePermissionDenied).toString(composeTestRule.activity)).assertExists()
        //Snackbar action is clicked
        composeTestRule.onNodeWithText(StringDesc.Resource(MR.strings.settings).toString(composeTestRule.activity)).assertExists()
        //User is redirected to settings
        getInstrumentation().waitForIdleSync()
        device.findObject(UiSelector().resourceId(txtEntityHeader).text(appName)).exists()
        device.findObject(UiSelector().packageName("com.android.settings")).clickAndWaitForNewWindow()
        //User allows permission in settings
        device.findObject(UiSelector().textMatches("Mikrofon")).clickAndWaitForNewWindow() //TODO language
        if (Build.VERSION.SDK_INT > 28) {
            //pre api 28 there is no further navigation necessary because its a toggle
            device.findObject(UiSelector().resourceId(radioBtnAllow)).click()
        }
        //User clicks back
        device.pressBack()
        device.pressBack()
        device.pressBack()

        //Permission is allowed
        getInstrumentation().waitForIdleSync()
        assertTrue { MicrophonePermission.granted.value }

         */
    }

}