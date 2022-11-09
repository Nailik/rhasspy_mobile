package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.settings.IndicationSettingsViewModel

class IndicationSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = IndicationSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            Surface(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag(TestTag.Background)
            ) {
                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalMainNavController provides navController
                ) {
                    WakeWordIndicationSettingsContent(viewModel)
                }
            }
        }

    }

    /**
     * wake up display disabled
     * visual disabled
     * sound disabled
     *
     * user clicks wake up display
     * wake up display is enabled
     * wake up display is saved
     *
     * user clicks visual
     * visual is enabled
     * visual is saved
     *
     * user clicks sound
     * sound is enabled
     * sound is saved
     */
    @Test
    fun testIndicationSettings() {

    }

    /**
     * Sound is disabled
     * sound settings invisible
     *
     * user clicks sound
     * sound is enabled
     * sound settings visible
     *
     *
     */
    @Test
    fun testSoundIndicationOptions() {

    }

}