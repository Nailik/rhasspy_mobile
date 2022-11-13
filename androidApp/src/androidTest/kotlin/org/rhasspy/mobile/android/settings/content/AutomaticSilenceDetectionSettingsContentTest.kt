package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.viewModels.settings.AutomaticSilenceDetectionSettingsViewModel

class AutomaticSilenceDetectionSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = AutomaticSilenceDetectionSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                AutomaticSilenceDetectionSettingsContent(viewModel)
            }
        }

    }

    /**
     * Automatic silence detection disabled
     * settings not visible
     *
     * user clicks automatic silence detection
     * Automatic silence detection enabled
     * Automatic silence detection enabled saved
     * settings visible
     *
     * silence detection time 2000
     * user changes silence detection time to 5000
     * silence detection time 5000 saved
     *
     * audio level threshold 40
     * user changes audio level threshold to 100
     * audio level threshold time 100 saved
     */
    @Test
    fun testContent() {

    }

    /**
     *
     */
    @Test
    fun testRecording() {

    }
}