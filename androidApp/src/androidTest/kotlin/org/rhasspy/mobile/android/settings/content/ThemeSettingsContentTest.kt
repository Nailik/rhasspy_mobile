package org.rhasspy.mobile.android.settings.content

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.assertBackgroundColor
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.DarkThemeColors
import org.rhasspy.mobile.android.theme.LightThemeColors
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.viewmodel.settings.ThemeSettingsViewModel
import kotlin.test.assertEquals

class ThemeSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = ThemeSettingsViewModel()

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
                    ThemeSettingsScreenItemContent(viewModel)
                }
            }
        }

    }

    /**
     * theme is system
     * system is selected
     * background is system
     *
     * User clicks dark theme
     * theme is dark theme
     * dark theme is selected
     * theme dark theme is saved
     * background is dark
     *
     * User clicks light theme
     * theme is light theme
     * light theme is selected
     * theme light theme is saved
     * background is light
     */
    @Test
    fun testTheme() = runBlocking {
        viewModel.selectThemeOption(ThemeOptions.System)
        isSystemInDarkTheme()
        //theme is system
        assertEquals(ThemeOptions.System, viewModel.themeOption.value)
        //system is selected
        composeTestRule.onNodeWithTag(ThemeOptions.System, true).onChildAt(0).assertIsSelected()
        //background is system
        composeTestRule.awaitIdle()
        if (isSystemInDarkTheme()) {
            composeTestRule.onNodeWithTag(TestTag.Background)
                .assertBackgroundColor(DarkThemeColors.surface)
        } else {
            composeTestRule.onNodeWithTag(TestTag.Background)
                .assertBackgroundColor(LightThemeColors.surface)
        }

        //User clicks dark theme
        composeTestRule.onNodeWithTag(ThemeOptions.Dark).performClick()
        //theme is dark theme
        assertEquals(ThemeOptions.Dark, viewModel.themeOption.value)
        //dark theme is selected
        composeTestRule.onNodeWithTag(ThemeOptions.Dark, true).onChildAt(0).assertIsSelected()
        //theme dark theme is saved
        var newViewModel = ThemeSettingsViewModel()
        assertEquals(ThemeOptions.Dark, newViewModel.themeOption.value)
        //background is dark
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Background)
            .assertBackgroundColor(DarkThemeColors.surface)

        //User clicks light theme
        composeTestRule.onNodeWithTag(ThemeOptions.Light).performClick()
        //theme is light theme
        assertEquals(ThemeOptions.Light, viewModel.themeOption.value)
        //light theme is selected
        composeTestRule.onNodeWithTag(ThemeOptions.Light, true).onChildAt(0).assertIsSelected()
        //theme light theme is saved
        newViewModel = ThemeSettingsViewModel()
        assertEquals(ThemeOptions.Light, newViewModel.themeOption.value)
        //background is light
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Background)
            .assertBackgroundColor(LightThemeColors.surface)
    }

    private fun isSystemInDarkTheme(): Boolean {
        val uiMode =
            ApplicationProvider.getApplicationContext<Context>().resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}