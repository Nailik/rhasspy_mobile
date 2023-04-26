package org.rhasspy.mobile.android.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.onNodeWithText
import org.rhasspy.mobile.android.utils.text
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests Microphone Permission requesting
 *
 * differences between api levels:
 * - 23 : allow, deny, deny always (checkbox)
 * - 29 : allow, deny, deny always (button)
 * - 30 : allow only while using the app, ask every time, don't allow
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MicrophonePermissionTest : KoinComponent {

    // activity necessary for permission
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    //.google is on devices with google play services and is missing on devices without play services
    private val permissionDialogPackageNameRegex = when {
        Build.VERSION.SDK_INT >= 29 -> ".*permissioncontroller"
        else -> ".*packageinstaller"
    }

    private val indexOffset = 1
    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val appName = "Rhasspy Mobile"

    private val btnPermissionAllowForegroundOnly =
        "com.android.permissioncontroller:id/permission_allow_foreground_only_button"
    private val btnPermissionAllowOneTime =
        "com.android.permissioncontroller:id/permission_allow_one_time_button"
    private val radioBtnAllow = "com.android.permissioncontroller:id/allow_radio_button"
    private val radioBtnAllowForegroundOnly =
        "com.android.permissioncontroller:id/allow_foreground_only_radio_button"
    private val txtEntityHeader = "com.android.settings:id/entity_header_title"

    private val cbhDoNotAsk = "com.android.packageinstaller:id/do_not_ask_checkbox"
    private val btnDoNotAskAgain =
        "com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button"

    private val allowPermissionRegex = ".*\\/permission_allow_button"
    private val denyPermissionRegex = ".*\\/permission_deny_button"

    private val btnRequestPermission = "btnRequestPermission"

    private var permissionResult = false

    private val systemSettingsListRegex = if (Build.VERSION.SDK_INT < 29) {
        ".*:id/list"
    } else {
        ".*:id/recycler_view"
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @NoLiveLiterals
    @Before
    fun setUp() {
        //set english
        LanguageSettingsViewModel().onEvent(SelectLanguageOption(LanguageType.English))

        //set content
        composeTestRule.activity.setContent {
            TestContentProvider {
                val snackbarHostState = LocalSnackbarHostState.current
                AppTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    ) {

                        RequiresMicrophonePermission(
                            MR.strings.defaultText.stable,
                            { permissionResult = true }) { onClick ->
                            Button(onClick = onClick) {
                                Text(btnRequestPermission)
                            }
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
    fun testAllow() = runTest {
        if (Build.VERSION.SDK_INT >= 30) {
            //no simple allow button on api 30
            return@runTest
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //User selects allow
        device.findObject(UiSelector().resourceIdMatches(allowPermissionRegex)).click()

        //Dialog is closed and permission granted
        composeTestRule.awaitIdle()
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertTrue { permissionResult }
        assertTrue { MicrophonePermission.granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * User selects allow while using the app
     * Dialog is closed and permission granted
     */
    @Test
    fun testAllowWhileUsing() = runTest {
        if (Build.VERSION.SDK_INT < 30) {
            //allow while using only available above and on api 30
            return@runTest
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //User selects allow while using the app
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowForegroundOnly)).click()

        //Dialog is closed and permission granted
        getInstrumentation().waitForIdleSync()
        composeTestRule.awaitIdle()
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
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
    fun testAllowOnce() = runTest {
        if (Build.VERSION.SDK_INT < 30) {
            //allow one time only available above and on api 30
            return@runTest
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()

        //System dialog is shown
        composeTestRule.awaitIdle()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //user selects only once
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowOneTime)).click()

        //Dialog is closed and permission granted
        composeTestRule.awaitIdle()
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertTrue { permissionResult }
        assertTrue { MicrophonePermission.granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * user selects deny
     *
     * User clicks button
     * Information dialog is shown
     * User clicks ok
     * user selects deny always
     * permission is denied
     *
     * User clicks button
     * Snack Bar is shown
     */
    @Test
    fun testDenyAlways() = runTest {
        if (Build.VERSION.SDK_INT >= 30) {
            //no always allow button above api 30
            return@runTest
        }

        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        //deny permission
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
            .clickAndWaitForNewWindow()
        composeTestRule.awaitIdle()

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission).assertExists()
        //ok clicked
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //deny always
        if (Build.VERSION.SDK_INT < 29) {
            //check do not ask again
            device.findObject(UiSelector().resourceId(cbhDoNotAsk)).click()
            //deny always
            device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
                .clickAndWaitForNewWindow()
        } else {
            //directly click do not ask again
            device.findObject(UiSelector().resourceId(btnDoNotAskAgain)).clickAndWaitForNewWindow()
        }

        //Dialog is closed and permission not granted
        composeTestRule.awaitIdle()
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertFalse { permissionResult }
        assertFalse { MicrophonePermission.granted.value }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        //snack bar shown
        assertTrue {
            device.findObject(UiSelector().text(MR.strings.microphonePermissionDenied.stable)).exists()
        }
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
    fun testInformationDialog() = runTest {
        permissionResult = false
        assertFalse { MicrophonePermission.granted.value }


        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        //user selects deny
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()
        //Dialog is closed and permission not granted
        composeTestRule.awaitIdle()
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertFalse { MicrophonePermission.granted.value }
        //Snackbar is shown
        assertTrue {
            device.findObject(UiSelector().text(MR.strings.microphonePermissionDenied.stable)).exists()
        }
        assertFalse { MicrophonePermission.granted.value }


        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()

        //on some devices it may not be required to show dialog
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                composeTestRule.activity,
                Manifest.permission.RECORD_AUDIO
            )
        ) {

            //InformationDialog is shown
            composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission)
                .assertExists()
            //Cancel clicked
            composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
            composeTestRule.awaitIdle()
            //Dialog closed
            composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission)
                .assertDoesNotExist()


            //User clicks button
            composeTestRule.onNodeWithText(btnRequestPermission).performClick()
            composeTestRule.awaitIdle()
            //InformationDialog is shown
            composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission)
                .assertExists()
            //ok clicked
            composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
            composeTestRule.awaitIdle()
            //Dialog closed
            composeTestRule.onNodeWithTag(TestTag.DialogInformationMicrophonePermission)
                .assertDoesNotExist()
            //System Dialog is shown
            getInstrumentation().waitForIdleSync()
            assertTrue {
                device.findObject(
                    UiSelector().packageNameMatches(
                        permissionDialogPackageNameRegex
                    )
                ).exists()
            }
            //user selects deny
            //deny always
            if (Build.VERSION.SDK_INT < 29) {
                //check do not ask again
                device.findObject(UiSelector().resourceId(cbhDoNotAsk)).click()
                //deny always
                device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
                    .clickAndWaitForNewWindow()
            } else {
                //directly click do not ask again
                device.findObject(UiSelector().resourceId(btnDoNotAskAgain))
                    .clickAndWaitForNewWindow()
            }
            getInstrumentation().waitForIdleSync()

        }

        //User clicks button
        composeTestRule.onNodeWithText(btnRequestPermission).performClick()
        composeTestRule.awaitIdle()
        //Snackbar is shown
        getInstrumentation().waitForIdleSync()
        assertTrue {
            device.findObject(UiSelector().text(MR.strings.microphonePermissionDenied.stable)).exists()
        }
        composeTestRule.waitForIdle()
        //Snackbar action is clicked
        composeTestRule.onNodeWithText(MR.strings.settings.stable).performClick()
        composeTestRule.awaitIdle()
        //User is redirected to settings
        getInstrumentation().waitForIdleSync()
        device.findObject(UiSelector().resourceId(txtEntityHeader).text(appName)).exists()


        //User allows permission in settings
        //open permissions page
        device.findObject(UiSelector().resourceIdMatches(systemSettingsListRegex))
            .getChild(UiSelector().clickable(true).index(if (Build.VERSION.SDK_INT == 27) 4 else 3))
            .clickAndWaitForNewWindow()
        getInstrumentation().waitForIdleSync()
        //click microphone permission
        if (Build.VERSION.SDK_INT < 29) {
            device.findObject(UiSelector().resourceIdMatches(systemSettingsListRegex))
                .getChild(UiSelector().clickable(true).index(indexOffset))
                .click()
        } else {
            device.findObject(UiSelector().resourceIdMatches(systemSettingsListRegex))
                .getChild(UiSelector().index((if (Build.VERSION.SDK_INT == 29) 2 else if (Build.VERSION.SDK_INT == 30) 4 else 3) + indexOffset))
                .clickAndWaitForNewWindow()
            if (Build.VERSION.SDK_INT < 30) {
                device.findObject(UiSelector().resourceId(radioBtnAllow)).click()
            } else {
                device.findObject(UiSelector().resourceId(radioBtnAllowForegroundOnly)).click()
            }
        }


        //User clicks back
        device.pressBack()
        device.pressBack()

        if (Build.VERSION.SDK_INT >= 24) {
            device.pressBack()
        }


        //Permission is allowed
        getInstrumentation().waitForIdleSync()
        assertTrue { MicrophonePermission.granted.value }
    }
}
