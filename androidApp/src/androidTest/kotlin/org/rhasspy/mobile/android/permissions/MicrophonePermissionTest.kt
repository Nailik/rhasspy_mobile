package org.rhasspy.mobile.android.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.app.MainActivity
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
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
@RunWith(AndroidJUnit4::class)
class MicrophonePermissionTest : KoinComponent {

    // activity necessary for permission
    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    //.google is on devices with google play services and is missing on devices without play services
    private val permissionDialogPackageNameRegex = when {
        Build.VERSION.SDK_INT >= 29 -> ".*permissioncontroller"
        else                        -> ".*packageinstaller"
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

    private val allowPermissionRegex = ".*/permission_allow_button"
    private val denyPermissionRegex = ".*/permission_deny_button"

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
        AppSetting.didShowCrashlyticsDialog.value = true
        AppSetting.didShowChangelogDialog.value = BuildConfig.VERSION_CODE
        LanguageSettingsViewModel(get()).onEvent(SelectLanguageOption(LanguageType.English))
    }

    /**
     * User clicks button
     * System dialog is shown
     * User selects allow
     * Dialog is closed and permission granted
     */
    @Test
    @AllowFlaky
    fun testAllow() {
        if (Build.VERSION.SDK_INT >= 30) {
            //no simple allow button on api 30
            return
        }

        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()

        //System dialog is shown
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertTrue {
            device.findObject(UiSelector().packageNameMatches(permissionDialogPackageNameRegex))
                .exists()
        }

        //User selects allow
        device.wait(Until.hasObject(By.res(allowPermissionRegex.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(allowPermissionRegex)).click()

        //Dialog is closed and permission granted
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertTrue { get<IMicrophonePermission>().granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * User selects allow while using the app
     * Dialog is closed and permission granted
     */
    @Test
    @AllowFlaky
    fun testAllowWhileUsing() {
        if (Build.VERSION.SDK_INT < 30) {
            //allow while using only available above and on api 30
            return
        }

        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //User selects allow while using the app
        device.wait(Until.hasObject(By.res(btnPermissionAllowForegroundOnly.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowForegroundOnly)).click()

        //Dialog is closed and permission granted
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.res(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertTrue { get<IMicrophonePermission>().granted.value }
    }

    /**
     * User clicks button
     * System dialog is shown
     * user selects only once
     * Dialog is closed and permission granted
     */
    @Test
    fun testAllowOnce() {
        if (Build.VERSION.SDK_INT < 30) {
            //allow one time only available above and on api 30
            return
        }

        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()

        //System dialog is shown
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }

        //user selects only once
        device.wait(Until.hasObject(By.res(btnPermissionAllowOneTime.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(btnPermissionAllowOneTime)).click()

        //Dialog is closed and permission granted
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertTrue { get<IMicrophonePermission>().granted.value }
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
    fun testDenyAlways() {
        if (Build.VERSION.SDK_INT >= 30) {
            //no always allow button above api 30
            return
        }

        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        //deny permission
        device.wait(Until.hasObject(By.res(denyPermissionRegex.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
            .clickAndWaitForNewWindow()

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
        //InformationDialog is shown
        composeTestRule.onNodeWithTag(TestTag.DialogMicrophonePermissionInfo).assertExists()
        //ok clicked
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
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
            device.wait(Until.hasObject(By.res(cbhDoNotAsk)), 5000)
            device.findObject(UiSelector().resourceId(cbhDoNotAsk)).click()
            //deny always
            device.wait(Until.hasObject(By.res(denyPermissionRegex.toPattern())), 5000)
            device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
                .clickAndWaitForNewWindow()
        } else {
            //directly click do not ask again
            device.wait(Until.hasObject(By.res(btnDoNotAskAgain)), 5000)
            device.findObject(UiSelector().resourceId(btnDoNotAskAgain)).clickAndWaitForNewWindow()
        }

        //Dialog is closed and permission not granted
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
        //snack bar shown
        device.wait(Until.hasObject(textRes(MR.strings.microphonePermissionDenied.stable)), 5000)
        assertTrue {
            device.findObject(UiSelector().text(MR.strings.microphonePermissionDenied.stable))
                .exists()
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
    fun testInformationDialog() {
        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
        device.waitForIdle()

        //System dialog is shown
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertTrue {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        //user selects deny
        device.wait(Until.hasObject(By.res(denyPermissionRegex.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex)).click()
        //Dialog is closed and permission not granted
        device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
        assertFalse {
            device.findObject(
                UiSelector().packageNameMatches(
                    permissionDialogPackageNameRegex
                )
            ).exists()
        }
        assertFalse { get<IMicrophonePermission>().granted.value }
        //Snack bar is shown
        device.wait(Until.hasObject(textRes(MR.strings.microphonePermissionDenied.stable)), 5000)
        assertFalse { get<IMicrophonePermission>().granted.value }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
        //on some devices it may not be required to show dialog
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                composeTestRule.activity,
                Manifest.permission.RECORD_AUDIO
            )
        ) {

            //InformationDialog is shown
            composeTestRule.onNodeWithTag(TestTag.DialogMicrophonePermissionInfo)
                .assertExists()
            //Cancel clicked
            composeTestRule.onNodeWithTag(TestTag.DialogCancel).performClick()
            //Dialog closed
            composeTestRule.onNodeWithTag(TestTag.DialogMicrophonePermissionInfo)
                .assertDoesNotExist()

            //User clicks button
            composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
            //InformationDialog is shown
            composeTestRule.onNodeWithTag(TestTag.DialogMicrophonePermissionInfo)
                .assertExists()
            //ok clicked
            composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
            //Dialog closed
            composeTestRule.onNodeWithTag(TestTag.DialogMicrophonePermissionInfo)
                .assertDoesNotExist()
            //System Dialog is shown
            getInstrumentation().waitForIdleSync()
            device.wait(Until.hasObject(By.pkg(permissionDialogPackageNameRegex.toPattern())), 5000)
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
                device.wait(Until.hasObject(By.res(cbhDoNotAsk)), 5000)
                device.findObject(UiSelector().resourceId(cbhDoNotAsk)).click()
                //deny always
                device.wait(Until.hasObject(By.res(denyPermissionRegex.toPattern())), 5000)
                device.findObject(UiSelector().resourceIdMatches(denyPermissionRegex))
                    .clickAndWaitForNewWindow()
            } else {
                //directly click do not ask again
                device.wait(Until.hasObject(By.res(btnDoNotAskAgain)), 5000)
                device.findObject(UiSelector().resourceId(btnDoNotAskAgain))
                    .clickAndWaitForNewWindow()
            }
        }

        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.MicrophoneFab).performClick()
        device.wait(Until.hasObject(textRes(MR.strings.microphonePermissionDenied.stable)), 5000)
        //Snackbar action is clicked
        composeTestRule.onAllNodesWithText(MR.strings.settings.stable)[1].performClick()
        //User is redirected to settings
        getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.res(txtEntityHeader)), 5000)
        device.findObject(UiSelector().resourceId(txtEntityHeader).text(appName)).exists()

        //User allows permission in settings
        //open permissions page
        device.wait(Until.hasObject(By.res(systemSettingsListRegex.toPattern())), 5000)
        device.findObject(UiSelector().resourceIdMatches(systemSettingsListRegex))
            .getChild(UiSelector().clickable(true).index(if (Build.VERSION.SDK_INT == 27) 4 else 3))
            .clickAndWaitForNewWindow()
        getInstrumentation().waitForIdleSync()
        //click microphone permission
        if (Build.VERSION.SDK_INT < 29) {
            device.wait(Until.hasObject(By.res(systemSettingsListRegex.toPattern())), 5000)
            device.findObject(UiSelector().resourceIdMatches(systemSettingsListRegex))
                .getChild(UiSelector().clickable(true).index(indexOffset))
                .click()
        } else {
            device.wait(Until.hasObject(By.res(systemSettingsListRegex.toPattern())), 5000)
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
    }
}
