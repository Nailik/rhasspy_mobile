package org.rhasspy.mobile.android.settings.content

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.content.LanguageSettingsScreenItemContent
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import kotlin.test.assertEquals

class LanguageSettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<LanguageSettingsViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                LanguageSettingsScreenItemContent()
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
    fun testLanguage() = runTest {
        viewModel.onEvent(SelectLanguageOption(LanguageType.English))

        //language is english
        assertEquals(LanguageType.English, viewModel.viewState.value.languageOption)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onListItemRadioButton().assertIsSelected()
        //title is "Language"
        composeTestRule.onNodeWithTag(TestTag.AppBarTitle).assertTextEquals("Language")
        //StringDesc is English
        assertEquals(LanguageType.English.code, StringDesc.localeType.systemLocale!!.language)

        //User clicks german
        composeTestRule.onNodeWithTag(LanguageType.German).performClick()
        composeTestRule.awaitIdle()
        //language is german

        composeTestRule.waitUntil(
            condition = { viewModel.viewState.value.languageOption == LanguageType.German },
            timeoutMillis = 5000
        )
        //german is selected
        composeTestRule.onNodeWithTag(LanguageType.German, true).onListItemRadioButton().assertIsSelected()
        //StringDesc is German
        assertEquals(LanguageType.German.code, StringDesc.localeType.systemLocale!!.language)
        //language german is saved
        var newViewModel = LanguageSettingsViewModel()
        assertEquals(LanguageType.German, newViewModel.viewState.value.languageOption)

        //User clicks english
        composeTestRule.onNodeWithTag(LanguageType.English).performClick()
        composeTestRule.awaitIdle()
        //language is english
        assertEquals(LanguageType.English, viewModel.viewState.value.languageOption)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onListItemRadioButton().assertIsSelected()
        //StringDesc is English
        assertEquals(LanguageType.English.code, StringDesc.localeType.systemLocale!!.language)
        //language english is saved
        newViewModel = LanguageSettingsViewModel()
        assertEquals(LanguageType.English, newViewModel.viewState.value.languageOption)
    }

}