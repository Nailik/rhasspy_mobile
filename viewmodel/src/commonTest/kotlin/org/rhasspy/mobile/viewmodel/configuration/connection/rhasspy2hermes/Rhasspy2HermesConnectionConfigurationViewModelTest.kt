package org.rhasspy.mobile.viewmodel.configuration.connection.rhasspy2hermes

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import  org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewState.Rhasspy2HermesConnectionConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Rhasspy2HermesConnectionConfigurationViewModelTest : AppTest() {

    private lateinit var rhasspy2HermesConnectionConfigurationViewModel: Rhasspy2HermesConnectionConfigurationViewModel

    private lateinit var initialHttpConfigurationData: Rhasspy2HermesConnectionConfigurationData
    private lateinit var httpConfigurationData: Rhasspy2HermesConnectionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialHttpConfigurationData = Rhasspy2HermesConnectionConfigurationData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false
        )

        httpConfigurationData = Rhasspy2HermesConnectionConfigurationData(
            host = getRandomString(5),
            timeout = 23456L,
            bearerToken = getRandomString(5),
            isSSLVerificationDisabled = true
        )

        rhasspy2HermesConnectionConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialHttpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)

        with(httpConfigurationData) {
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(SetRhasspy2HermesSSLVerificationDisabled(isSSLVerificationDisabled))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesServerEndpointHost(host))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesTimeout(timeout.toString()))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)

        rhasspy2HermesConnectionConfigurationViewModel.onEvent(Save)

        assertEquals(httpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialHttpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)

        with(httpConfigurationData) {
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(SetRhasspy2HermesSSLVerificationDisabled(isSSLVerificationDisabled))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesServerEndpointHost(host))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesTimeout(timeout.toString()))
            rhasspy2HermesConnectionConfigurationViewModel.onEvent(UpdateRhasspy2HermesAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)

        rhasspy2HermesConnectionConfigurationViewModel.onEvent(Discard)

        assertEquals(initialHttpConfigurationData, rhasspy2HermesConnectionConfigurationViewModel.viewState.value.editData)
    }
}