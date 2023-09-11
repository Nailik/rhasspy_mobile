package org.rhasspy.mobile.viewmodel.configuration.connection.rhasspy3wyoming

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import  org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewState.Rhasspy3WyomingConnectionConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Rhasspy3WyomingConnectionConfigurationViewModelTest : AppTest() {

    private lateinit var rhasspy3WyomingConnectionConfigurationViewModel: Rhasspy3WyomingConnectionConfigurationViewModel

    private lateinit var initialHttpConfigurationData: Rhasspy3WyomingConnectionConfigurationData
    private lateinit var httpConfigurationData: Rhasspy3WyomingConnectionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialHttpConfigurationData = Rhasspy3WyomingConnectionConfigurationData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = true
        )

        httpConfigurationData = Rhasspy3WyomingConnectionConfigurationData(
            host = getRandomString(5),
            timeout = 23456L,
            bearerToken = getRandomString(5),
            isSSLVerificationDisabled = false
        )

        rhasspy3WyomingConnectionConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialHttpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)

        with(httpConfigurationData) {
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(SetRhasspy3WyomingSSLVerificationDisabled(isSSLVerificationDisabled))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingServerEndpointHost(host))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingTimeout(timeout.toString()))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)

        rhasspy3WyomingConnectionConfigurationViewModel.onEvent(Save)

        assertEquals(httpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialHttpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)

        with(httpConfigurationData) {
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(SetRhasspy3WyomingSSLVerificationDisabled(isSSLVerificationDisabled))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingServerEndpointHost(host))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingTimeout(timeout.toString()))
            rhasspy3WyomingConnectionConfigurationViewModel.onEvent(UpdateRhasspy3WyomingAccessToken(bearerToken))
        }

        assertEquals(httpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)

        rhasspy3WyomingConnectionConfigurationViewModel.onEvent(Discard)

        assertEquals(initialHttpConfigurationData, rhasspy3WyomingConnectionConfigurationViewModel.viewState.value.editData)
    }
}