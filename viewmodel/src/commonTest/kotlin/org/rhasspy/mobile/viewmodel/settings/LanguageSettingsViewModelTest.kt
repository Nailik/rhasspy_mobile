package org.rhasspy.mobile.viewmodel.settings

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var languageUtils: ILanguageUtils

    private lateinit var appearanceSettingsViewModel: AppearanceSettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { languageUtils }
            }
        )

        appearanceSettingsViewModel = get()
    }

    @Test
    fun `when user selects language it's saved and updated in the system`() {
        every { languageUtils.setLanguage(isAny()) } returns Unit

        LanguageType.entries.forEach { language ->
            appearanceSettingsViewModel.onEvent(SelectLanguageOption(language))

            assertEquals(language, AppSetting.languageType.value)
            verify { languageUtils.setLanguage(language) }
        }
    }

}