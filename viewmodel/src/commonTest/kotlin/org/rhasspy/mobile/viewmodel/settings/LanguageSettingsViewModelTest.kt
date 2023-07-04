package org.rhasspy.mobile.viewmodel.settings

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var languageUtils: ILanguageUtils

    private lateinit var languageSettingsViewModel: LanguageSettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

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
        every { languageUtils.setLanguage(isAny()) } returns Unit

        LanguageType.values().forEach { language ->
            languageSettingsViewModel.onEvent(SelectLanguageOption(language))

            assertEquals(language, AppSetting.languageType.value)
            verify { languageUtils.setLanguage(language) }
        }
    }

}