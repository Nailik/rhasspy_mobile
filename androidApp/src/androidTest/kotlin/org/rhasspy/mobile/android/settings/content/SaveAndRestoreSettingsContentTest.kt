package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.viewmodel.settings.SaveAndRestoreSettingsViewModel

class SaveAndRestoreSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = SaveAndRestoreSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                SaveAndRestoreSettingsContent(viewModel)
            }
        }

    }

}