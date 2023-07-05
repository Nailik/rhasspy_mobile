package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewState.RemoteHermesHttpConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteHermesHttpConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication
    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var remoteHermesHttpConfigurationViewModel: RemoteHermesHttpConfigurationViewModel

    private lateinit var initialRemoteHermesHttpConfigurationData: RemoteHermesHttpConfigurationData
    private lateinit var remoteHermesHttpConfigurationData: RemoteHermesHttpConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialRemoteHermesHttpConfigurationData = RemoteHermesHttpConfigurationData(
            httpClientServerEndpointHost = "",
            httpClientServerEndpointPort = 12101,
            httpClientTimeout = 30000L,
            isHttpSSLVerificationDisabled = true
        )

        remoteHermesHttpConfigurationData = RemoteHermesHttpConfigurationData(
            httpClientServerEndpointHost = getRandomString(5),
            httpClientServerEndpointPort = 3245,
            httpClientTimeout = 23456L,
            isHttpSSLVerificationDisabled = false
        )

        remoteHermesHttpConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialRemoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)

        with(remoteHermesHttpConfigurationData) {
            remoteHermesHttpConfigurationViewModel.onEvent(SetHttpSSLVerificationDisabled(isHttpSSLVerificationDisabled))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientServerEndpointHost(httpClientServerEndpointHost))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientServerEndpointPort(httpClientServerEndpointPort.toString()))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientTimeout(httpClientTimeout.toString()))
        }

        assertEquals(remoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)

        remoteHermesHttpConfigurationViewModel.onEvent(Save)

        assertEquals(remoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)
        assertEquals(remoteHermesHttpConfigurationData, RemoteHermesHttpConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialRemoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)

        with(remoteHermesHttpConfigurationData) {
            remoteHermesHttpConfigurationViewModel.onEvent(SetHttpSSLVerificationDisabled(isHttpSSLVerificationDisabled))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientServerEndpointHost(httpClientServerEndpointHost))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientServerEndpointPort(httpClientServerEndpointPort.toString()))
            remoteHermesHttpConfigurationViewModel.onEvent(UpdateHttpClientTimeout(httpClientTimeout.toString()))
        }

        assertEquals(remoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)

        remoteHermesHttpConfigurationViewModel.onEvent(Discard)

        assertEquals(initialRemoteHermesHttpConfigurationData, remoteHermesHttpConfigurationViewModel.viewState.value.editData)
        assertEquals(initialRemoteHermesHttpConfigurationData, RemoteHermesHttpConfigurationData())
    }
}