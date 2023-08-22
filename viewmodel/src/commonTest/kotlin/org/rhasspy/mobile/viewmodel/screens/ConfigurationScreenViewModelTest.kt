package org.rhasspy.mobile.viewmodel.screens

import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.ScrollToErrorClick
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Consumed
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ConfigurationScreenViewModelTest : AppTest() {

    @Mock
    lateinit var httpClientService: IHttpClientService

    private lateinit var configurationScreenViewModel: ConfigurationScreenViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { httpClientService }
            }
        )

        every { httpClientService.serviceState } returns MutableStateFlow(ServiceState.Exception()).readOnly
        configurationScreenViewModel = get()
    }

    @Test
    fun `when user changes siteId the configuration is updated`() {
        val newSiteId = getRandomString(5)

        configurationScreenViewModel.onEvent(SiteIdChange(newSiteId))

        assertEquals(newSiteId, ConfigurationSetting.siteId.value)
    }

    @Test
    fun `when the user scrolls to error the event is consumed and reset`() {
        configurationScreenViewModel.onEvent(ScrollToErrorClick)

        assertNotEquals(null, configurationScreenViewModel.viewState.value.scrollToError)

        configurationScreenViewModel.onEvent(Consumed.ScrollToError)

        assertEquals(null, configurationScreenViewModel.viewState.value.scrollToError)
    }

}