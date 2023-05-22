package org.rhasspy.mobile.android.settings.content

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.content.AudioRecorderSettingsContent
import kotlin.test.assertEquals

class AudioRecorderSettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                AudioRecorderSettingsContent()
            }
        }

    }

    /**
     * default sample rate is selected
     * for each sample rate:
     * click element
     * element is saved
     * element is selected
     *
     * default channel is selected
     * for each channel:
     * click element
     * element is saved
     * element is selected
     *
     * default encoding is selected
     * for each encoding:
     * click element
     * element is saved
     * element is selected
     */
    @Test
    fun testContent() = runTest {
        //default sample rate is selected
        assertEquals(AudioRecorderSampleRateType.default, AppSetting.audioRecorderSampleRate.value)
        composeTestRule.onNodeWithCombinedTag(AudioRecorderSampleRateType.default, TestTag.AudioRecorderSampleRateType, true).performScrollTo().onListItemRadioButton().assertIsSelected()
        //for each sample rate:
        AudioRecorderSampleRateType.values().forEach { element ->
            //click element
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderSampleRateType, true).performScrollTo().performClick()
            composeTestRule.awaitIdle()
            //element is saved
            assertEquals(element, AppSetting.audioRecorderSampleRate.value)
            //element is selected
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderSampleRateType, true).onListItemRadioButton().assertIsSelected()
        }

        //default channel is selected
        assertEquals(AudioRecorderChannelType.default, AppSetting.audioRecorderChannel.value)
        composeTestRule.onNodeWithTag(TestTag.AudioRecorderChannelType, true).performScrollTo()
        composeTestRule.onNodeWithCombinedTag(AudioRecorderChannelType.default, TestTag.AudioRecorderChannelType, true).performScrollTo().onListItemRadioButton().assertIsSelected()
        //for each channel:
        AudioRecorderChannelType.values().forEach { element ->
            //click element
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderChannelType, true).performScrollTo().performClick()
            composeTestRule.awaitIdle()
            //element is saved
            assertEquals(element, AppSetting.audioRecorderChannel.value)
            //element is selected
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderChannelType, true).onListItemRadioButton().assertIsSelected()
        }

        //default encoding is selected
        assertEquals(AudioRecorderEncodingType.default, AppSetting.audioRecorderEncoding.value)
        composeTestRule.onNodeWithTag(TestTag.AudioRecorderEncodingType, true).performScrollTo()
        composeTestRule.onNodeWithCombinedTag(AudioRecorderEncodingType.default, TestTag.AudioRecorderEncodingType, true).performScrollTo().onListItemRadioButton().assertIsSelected()
        //for each encoding:
        AudioRecorderEncodingType.supportedValues().forEach { element ->
            //click element
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderEncodingType, true).performScrollTo().performClick()
            composeTestRule.awaitIdle()
            //element is saved
            assertEquals(element, AppSetting.audioRecorderEncoding.value)
            //element is selected
            composeTestRule.onNodeWithCombinedTag(element, TestTag.AudioRecorderEncodingType, true).onListItemRadioButton().assertIsSelected()
        }
    }
}