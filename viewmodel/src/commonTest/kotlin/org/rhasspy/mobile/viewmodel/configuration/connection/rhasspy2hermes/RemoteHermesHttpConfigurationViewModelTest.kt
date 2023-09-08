/*package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewState.HttpConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteHermesHttpConfigurationViewModelTest : AppTest() {

    private lateinit var remoteHermesHttpConfigurationViewModel: HttpConnectionDetailConfigurationViewModel

    private lateinit var initialHttpConfigurationData: HttpConfigurationData
    private lateinit var httpConfigurationData: HttpConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialHttpConfigurationData = HttpConfigurationData(
            host = "",
            port = 12101,
            timeout = 30000L,
            isSSLVerificationDisabled = true
        )

        httpConfigurationData = HttpConfigurationData(
            host = getRandomString(5),
            port = 3245,
            timeout = 23456L,
            isSSLVerificationDisabled = false
        )

        remoteHermesHttpConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialHttpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )

        with(httpConfigurationData) {
            remoteHermesHttpConfigurationViewModel.onEvent(
                SetHomeAssistantSSLVerificationDisabled(
                    isSSLVerificationDisabled
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(
                UpdateHomeAssistantClientServerEndpointHost(
                    host
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(
                UpdateHttpClientServerEndpointPort(
                    port.toString()
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHomeAssistantClientTimeout(timeout.toString()))
        }

        assertEquals(
            httpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )

        remoteHermesHttpConfigurationViewModel.onEvent(Save)

        assertEquals(
            httpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )
        assertEquals(httpConfigurationData, HttpConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialHttpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )

        with(httpConfigurationData) {
            remoteHermesHttpConfigurationViewModel.onEvent(
                SetHomeAssistantSSLVerificationDisabled(
                    isSSLVerificationDisabled
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(
                UpdateHomeAssistantClientServerEndpointHost(
                    host
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(
                UpdateHttpClientServerEndpointPort(
                    port.toString()
                )
            )
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHomeAssistantClientTimeout(timeout.toString()))
        }

        assertEquals(
            httpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )

        remoteHermesHttpConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialHttpConfigurationData,
            remoteHermesHttpConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialHttpConfigurationData, HttpConfigurationData())
    }
}*/