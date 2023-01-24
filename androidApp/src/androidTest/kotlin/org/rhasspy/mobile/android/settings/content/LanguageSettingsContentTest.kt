package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.logic.settings.types.LanguageType
import org.rhasspy.mobile.viewmodel.settings.LanguageSettingsViewModel
import kotlin.test.assertEquals

class LanguageSettingsContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = LanguageSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                LanguageSettingsScreenItemContent(viewModel)
            }
        }

    }

    /**
     * language is english
     * english is selected
     * title is "Language"
     * StringDesc is English
     *
     * User clicks german
     * language is german
     * german is selected
     * title is "Sprache"
     * StringDesc is German
     * language german is saved
     *
     * User clicks english
     * language is english
     * english is selected
     * title is "Sprache"
     * StringDesc is English
     * language english is saved
     */
    @Test
    fun testLanguage() = runBlocking {
        viewModel.selectLanguageOption(LanguageType.English)

        //language is english
        assertEquals(LanguageType.English, viewModel.languageOption.value)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onChildAt(0).assertIsSelected()
        //title is "Language"
        composeTestRule.onNodeWithTag(TestTag.AppBarTitle).assertTextEquals("Language")
        //StringDesc is English
        assertEquals(LanguageType.English.code, StringDesc.localeType.systemLocale!!.language)

        //User clicks german
        composeTestRule.onNodeWithTag(LanguageType.German).performClick()
        composeTestRule.awaitIdle()
        //language is german

        composeTestRule.waitUntil(
            condition = { viewModel.languageOption.value == LanguageType.German },
            timeoutMillis = 5000
        )
        //german is selected
        composeTestRule.onNodeWithTag(LanguageType.German, true).onChildAt(0).assertIsSelected()
        //StringDesc is German
        assertEquals(LanguageType.German.code, StringDesc.localeType.systemLocale!!.language)
        //language german is saved
        var newViewModel = LanguageSettingsViewModel()
        assertEquals(LanguageType.German, newViewModel.languageOption.value)

        //User clicks english
        composeTestRule.onNodeWithTag(LanguageType.English).performClick()
        //language is english
        assertEquals(LanguageType.English, viewModel.languageOption.value)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onChildAt(0).assertIsSelected()
        //StringDesc is English
        assertEquals(LanguageType.English.code, StringDesc.localeType.systemLocale!!.language)
        //language english is saved
        newViewModel = LanguageSettingsViewModel()
        assertEquals(LanguageType.English, newViewModel.languageOption.value)
    }

}