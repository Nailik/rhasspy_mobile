package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.AudioPlayingConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.SelectEditAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import kotlin.test.assertEquals

class AudioPlayingConfigurationContentTest : FlakyTest() {


    private val viewModel = get<AudioPlayingConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        AudioPlayingConfigurationScreen(viewModel)
    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * User clicks save
     * option is saved to remote http
     */
    @Test
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectEditAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AudioPlayingOption.Rhasspy2HermesHttp).onListItemRadioButton()
            .performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            AudioPlayingOption.Rhasspy2HermesHttp,
            viewModel.viewState.value.editData.audioPlayingOption
        )
        composeTestRule.onNodeWithTag(AudioPlayingOption.Rhasspy2HermesHttp).onListItemRadioButton()
            .assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        AudioPlayingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(AudioPlayingOption.Rhasspy2HermesHttp, it.audioPlayingOption)
        }
    }

    /**
     * output is set to sound
     *
     * option disable is set
     * output options not visible
     * User clicks option local
     * new option is selected
     *
     * output options visible
     * option sound is set
     *
     * user clicks option notification
     * option notification is selected
     *
     * User clicks save
     * option is saved to local
     * option notification is saved
     */
    @Test
    @AllowFlaky
    fun testLocalOutput() = runTest {
        setupContent()

        viewModel.onEvent(SelectEditAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //output is set to sound
        assertEquals(AudioOutputOption.Sound, viewModel.viewState.value.editData.audioOutputOption)

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled, true).performScrollTo()
            .onListItemRadioButton().assertIsSelected()
        //output options not visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()

        //User clicks option local
        composeTestRule.onNodeWithTag(AudioPlayingOption.Local).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            AudioPlayingOption.Local,
            viewModel.viewState.value.editData.audioPlayingOption
        )

        //output options visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        //option sound is set
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onListItemRadioButton()
            .assertIsSelected()

        //user clicks option notification
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification).performClick()
        //option notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        AudioPlayingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to local
            assertEquals(AudioPlayingOption.Local, it.audioPlayingOption)
            //option notification is saved
            assertEquals(AudioOutputOption.Notification, it.audioOutputOption)
        }
    }
}