package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.ui.settings.AppearanceSettingsScreenItemContent
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsViewModel
import kotlin.test.assertEquals

class AppearanceSettingsContentTest : FlakyTest() {

    private val viewModel = get<AppearanceSettingsViewModel>()

    @Composable
    override fun ComposableContent() {
        AppearanceSettingsScreenItemContent(viewModel)
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
    @AllowFlaky
    fun testLanguage() = runTest {
        setupContent()

        viewModel.onEvent(SelectLanguageOption(LanguageType.English))

        //language is english
        assertEquals(LanguageType.English, viewModel.viewState.value.languageOption)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onListItemRadioButton()
            .assertIsSelected()
        //StringDesc is English
        composeTestRule.awaitIdle()
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
        composeTestRule.onNodeWithTag(LanguageType.German, true).onListItemRadioButton()
            .assertIsSelected()
        //StringDesc is German
        composeTestRule.awaitIdle()
        assertEquals(LanguageType.German.code, StringDesc.localeType.systemLocale!!.language)
        //language german is saved
        var newViewModel = AppearanceSettingsViewModel(get())
        assertEquals(LanguageType.German, newViewModel.viewState.value.languageOption)

        //User clicks english
        composeTestRule.onNodeWithTag(LanguageType.English).performClick()
        composeTestRule.awaitIdle()
        //language is english
        assertEquals(LanguageType.English, viewModel.viewState.value.languageOption)
        //english is selected
        composeTestRule.onNodeWithTag(LanguageType.English, true).onListItemRadioButton()
            .assertIsSelected()
        //StringDesc is English
        assertEquals(LanguageType.English.code, StringDesc.localeType.systemLocale!!.language)
        //language english is saved
        newViewModel = AppearanceSettingsViewModel(get())
        assertEquals(LanguageType.English, newViewModel.viewState.value.languageOption)
    }

}