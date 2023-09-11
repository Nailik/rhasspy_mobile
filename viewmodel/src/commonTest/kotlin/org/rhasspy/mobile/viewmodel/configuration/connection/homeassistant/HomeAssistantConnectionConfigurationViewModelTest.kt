package org.rhasspy.mobile.viewmodel.configuration.connection.homeassistant

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewState.HomeAssistantConnectionConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeAssistantConnectionConfigurationViewModelTest : AppTest() {

    private lateinit var homeAssistantConnectionConfigurationViewModel: HomeAssistantConnectionConfigurationViewModel

    private lateinit var initialHttpConfigurationData: HomeAssistantConnectionConfigurationData
    private lateinit var httpConfigurationData: HomeAssistantConnectionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialHttpConfigurationData = HomeAssistantConnectionConfigurationData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = true
        )

        httpConfigurationData = HomeAssistantConnectionConfigurationData(
            host = getRandomString(5),
            timeout = 23456L,
            bearerToken = getRandomString(5),
            isSSLVerificationDisabled = false
        )

        homeAssistantConnectionConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialHttpConfigurationData,
            homeAssistantConnectionConfigurationViewModel.viewState.value.editData
        )

        with(httpConfigurationData) {
            homeAssistantConnectionConfigurationViewModel.onEvent(SetHomeAssistantSSLVerificationDisabled(isSSLVerificationDisabled))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantClientServerEndpointHost(host))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantClientTimeout(timeout.toString()))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, homeAssistantConnectionConfigurationViewModel.viewState.value.editData)

        homeAssistantConnectionConfigurationViewModel.onEvent(Save)

        assertEquals(httpConfigurationData, homeAssistantConnectionConfigurationViewModel.viewState.value.editData)
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialHttpConfigurationData,
            homeAssistantConnectionConfigurationViewModel.viewState.value.editData
        )

        with(httpConfigurationData) {
            homeAssistantConnectionConfigurationViewModel.onEvent(SetHomeAssistantSSLVerificationDisabled(isSSLVerificationDisabled))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantClientServerEndpointHost(host))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantClientTimeout(timeout.toString()))
            homeAssistantConnectionConfigurationViewModel.onEvent(UpdateHomeAssistantAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, homeAssistantConnectionConfigurationViewModel.viewState.value.editData)

        homeAssistantConnectionConfigurationViewModel.onEvent(Discard)

        assertEquals(initialHttpConfigurationData, homeAssistantConnectionConfigurationViewModel.viewState.value.editData)
    }
}