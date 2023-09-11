package org.rhasspy.mobile.viewmodel.screens

import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigurationScreenViewModelTest : AppTest() {

    private lateinit var configurationScreenViewModel: ConfigurationScreenViewModel

    override fun setUpMocks() {}

    @BeforeTest
    fun before() {
        super.before(
            module {
            }
        )

        configurationScreenViewModel = get()
    }

    @Test
    fun `when user changes siteId the configuration is updated`() {
        val newSiteId = getRandomString(5)

        configurationScreenViewModel.onEvent(SiteIdChange(newSiteId))

        assertEquals(newSiteId, ConfigurationSetting.siteId.value)
    }

}