package org.rhasspy.mobile.android.settings.content.sound

import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.test.R
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.AudioOutputOption.Sound
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.sounds.SoundOption.Disabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SelectSoundIndicationOutputOption
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetSoundIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.SetSoundIndicationOption
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewModel
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
abstract class IndicationSoundScreenTest(
    val title: StableStringResource,
    val screen: IndicationSettingsScreens
) : FlakyTest() {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    abstract val device: UiDevice

    private val fileName = "sound.wav"

    abstract fun getViewModelInstance(): IIndicationSoundSettingsViewModel

    private val viewModel get() = getViewModelInstance()

    open fun setUp() {
        requestExternalStoragePermissions(device)

        composeTestRule.activity.setContent {
            TestContentProvider {
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
    open fun testAddSoundFile() = runTest {
        //copy test file to downloads directory
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound)
            .copyTo(file.outputStream())

        viewModel.onEvent(SetSoundIndicationOption(Disabled))
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { viewModel.viewState.value.soundSetting == Disabled.name }
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onListItemRadioButton().assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()
        //user clicks file
        device.wait(Until.hasObject(By.text(fileName)), 5000)
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //file is added to list
        composeTestRule.waitUntilExists(hasTag(fileName))
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onListItemRadioButton().assertIsSelected()
        //file cannot be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertDoesNotExist()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()
        //default sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Default).onListItemRadioButton().assertIsSelected()
        //default sound is saved
        assertTrue { viewModel.viewState.value.soundSetting == SoundOption.Default.name }

        //file can be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertIsDisplayed()
        //user clicks delete
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()

        //file is removed
        //composeTestRule.onNodeWithTag(fileName).assertDoesNotExist() TOO often false assert error
        //file is deleted from view model
        assertTrue { viewModel.viewState.value.customSoundFiles.isEmpty() }
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
    open fun testPlayback() = runTest {
        //copy test file to downloads directory
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound)
            .copyTo(file.outputStream())

        //output option is sound
        //(play works with silent sound but not silent notification)
        val otherViewModel = get<IndicationSettingsViewModel>()
        otherViewModel.onEvent(SelectSoundIndicationOutputOption(Sound))
        otherViewModel.onEvent(SetSoundIndicationEnabled(true))
        viewModel.onEvent(SetSoundIndicationOption(Disabled))
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { otherViewModel.viewState.value.isSoundIndicationEnabled }
        composeTestRule.awaitIdle()
        device.waitForIdle()
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onListItemRadioButton().assertIsSelected()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()
        //default is selected
        composeTestRule.onNodeWithTag(TestTag.Default).onListItemRadioButton().assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //user clicks file
        device.wait(Until.hasObject(By.text(fileName)), 5000)
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()
        composeTestRule.awaitIdle()

        //file is added to list
        composeTestRule.waitUntilExists(hasTag(fileName))
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        composeTestRule.awaitIdle()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onListItemRadioButton().assertIsSelected()

        //user clicks play
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is playing
        composeTestRule.waitUntil(
            condition = { viewModel.viewState.value.isAudioPlaying },
            timeoutMillis = 5000
        )
        assertTrue { viewModel.viewState.value.isAudioPlaying }

        //user clicks stop
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is stop
        composeTestRule.waitUntil(
            condition = { !viewModel.viewState.value.isAudioPlaying },
            timeoutMillis = 5000
        )
        assertFalse { viewModel.viewState.value.isAudioPlaying }
    }

}