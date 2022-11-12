package org.rhasspy.mobile.android.settings.content.sound

import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.test.R
import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.viewModels.settings.IndicationSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.IIndicationSoundSettingsViewModel
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class IndicationSoundScreenTest(
    val viewModel: IIndicationSoundSettingsViewModel,
    val title: StringResource,
    val screen: IndicationSettingsScreens
) {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    abstract val device: UiDevice

    private val fileName = "sound.wav"

    open fun setUp() {
        requestExternalStoragePermissions(device)

        composeTestRule.activity.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                IndicationSoundScreen(
                    viewModel = viewModel,
                    title = title,
                    screen = screen
                )
            }
        }
    }

    /**
     * Disabled sound is saved
     * Disabled sound ist selected
     *
     * user selects add file
     * user clicks file
     *
     * file is added to list
     * file is selected
     * file cannot be deleted
     *
     * user clicks default
     * default sound ist selected
     * default sound is saved
     *
     * file can be deleted
     * user clicks delete
     *
     * file is removed
     * file is deleted from view model
     */
    open fun testAddSoundFile() = runBlocking {
        //copy test file to downloads directory
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound).copyTo(file.outputStream())

        viewModel.onClickSoundIndicationDisabled()
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { viewModel.isSoundIndicationDisabled.value }
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onChildAt(0).assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //user clicks file
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //file is added to list
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onChildAt(0).assertIsSelected()
        //file cannot be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertDoesNotExist()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        //default sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Default).onChildAt(0).assertIsSelected()
        //default sound is saved
        assertTrue { viewModel.isSoundIndicationDefault.value }

        //file can be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertIsDisplayed()
        //user clicks delete
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).performClick()
        composeTestRule.awaitIdle()
        awaitFrame()

        //file is removed
        composeTestRule.onNodeWithTag(fileName).assertDoesNotExist()
        //file is deleted from view model
        assertTrue { viewModel.customSoundFiles.value.isEmpty() }
    }

    /**
     * output option is sound
     * (play works with silent sound but not silent notification)
     *
     * Disabled sound is saved
     * Disabled sound ist selected
     *
     * user clicks default
     * default is selected
     *
     * user selects add file
     * user clicks file
     *
     * file is added to list
     * file is selected
     *
     * user clicks play
     * status is playing
     *
     * user clicks stop
     * status is stop
     */
    open fun testPlayback() = runBlocking {
        //copy test file to downloads directory
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound).copyTo(file.outputStream())

        //output option is sound
        //(play works with silent sound but not silent notification)
        IndicationSettingsViewModel().selectSoundIndicationOutputOption(AudioOutputOptions.Sound)

        viewModel.onClickSoundIndicationDisabled()
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { viewModel.isSoundIndicationDisabled.value }
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onChildAt(0).assertIsSelected()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        //default is selected
        composeTestRule.onNodeWithTag(TestTag.Default).onChildAt(0).assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //user clicks file
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //file is added to list
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onChildAt(0).assertIsSelected()

        //user clicks play
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is playing
        composeTestRule.waitUntil(
            condition = { viewModel.isAudioPlaying.value },
            timeoutMillis = 5000
        )
        assertTrue { viewModel.isAudioPlaying.value }

        //user clicks stop
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is stop
        composeTestRule.waitUntil(
            condition = { !viewModel.isAudioPlaying.value },
            timeoutMillis = 5000
        )
        assertFalse { viewModel.isAudioPlaying.value }
    }

}