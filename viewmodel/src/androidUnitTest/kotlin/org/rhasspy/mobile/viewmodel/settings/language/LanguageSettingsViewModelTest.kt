package org.rhasspy.mobile.viewmodel.settings.language

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.platformspecific.language.LanguageUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageSettingsViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var languageUtils: LanguageUtils

    private lateinit var languageSettingsViewModel: LanguageSettingsViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { languageUtils }
            }
        )

        languageSettingsViewModel = get()
    }

    @Test
    fun `when user selects language it's saved and updated in the system`() {
        LanguageType.values().forEach { language ->
            languageSettingsViewModel.onEvent(SelectLanguageOption(language))

            assertEquals(language, AppSetting.languageType.value)
            verify { languageUtils.setLanguage(language) }
        }
    }

}