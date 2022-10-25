package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

class WakeWordConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                WakeWordConfigurationContent(viewModel)
            }
        }

    }

}