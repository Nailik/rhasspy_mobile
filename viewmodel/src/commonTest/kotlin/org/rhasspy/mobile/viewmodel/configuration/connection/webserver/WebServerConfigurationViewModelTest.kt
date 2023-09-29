package org.rhasspy.mobile.viewmodel.configuration.connection.webserver

import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewState.WebServerConnectionConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WebServerConfigurationViewModelTest : AppTest() {

    private lateinit var webServerConfigurationViewModel: WebServerConnectionConfigurationViewModel

    private lateinit var initialWebServerConfigurationData: WebServerConnectionConfigurationData
    private lateinit var webServerConfigurationData: WebServerConnectionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialWebServerConfigurationData = WebServerConnectionConfigurationData(
            isEnabled = true,
            port = 12101,
            isSSLEnabled = false,
            keyStoreFile = null,
            keyStorePassword = "",
            keyAlias = "",
            keyPassword = ""
        )

        webServerConfigurationData = WebServerConnectionConfigurationData(
            isEnabled = false,
            port = 4569,
            isSSLEnabled = true,
            keyStoreFile = getRandomString(5),
            keyStorePassword = getRandomString(5),
            keyAlias = getRandomString(5),
            keyPassword = getRandomString(5)
        )

        webServerConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialWebServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)

        with(webServerConfigurationData) {
            webServerConfigurationViewModel.onEvent(SetHttpServerEnabled(isEnabled))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLEnabled(isSSLEnabled))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyAlias(keyAlias))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyPassword(keyPassword))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyStorePassword(keyStorePassword))
            webServerConfigurationViewModel.onEvent(UpdateHttpServerPort(port.toString()))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLKeyStoreFile(keyStoreFile?.toPath()!!))
        }

        assertEquals(webServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)

        webServerConfigurationViewModel.onEvent(Save)

        assertEquals(webServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)
        assertEquals(webServerConfigurationData, WebServerConnectionConfigurationDataMapper().invoke(ConfigurationSetting.localWebserverConnection.value))
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialWebServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)

        with(webServerConfigurationData) {
            webServerConfigurationViewModel.onEvent(SetHttpServerEnabled(isEnabled))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLEnabled(isSSLEnabled))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyAlias(keyAlias))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyPassword(keyPassword))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyStorePassword(keyStorePassword))
            webServerConfigurationViewModel.onEvent(UpdateHttpServerPort(port.toString()))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLKeyStoreFile(keyStoreFile?.toPath()!!))
        }

        assertEquals(webServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)

        webServerConfigurationViewModel.onEvent(Discard)

        assertEquals(initialWebServerConfigurationData, webServerConfigurationViewModel.viewState.value.editData)
        assertEquals(initialWebServerConfigurationData, WebServerConnectionConfigurationDataMapper().invoke(ConfigurationSetting.localWebserverConnection.value))
    }
}