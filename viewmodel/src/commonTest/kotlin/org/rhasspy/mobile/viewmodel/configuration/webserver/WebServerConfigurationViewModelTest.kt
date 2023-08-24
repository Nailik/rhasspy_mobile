package org.rhasspy.mobile.viewmodel.configuration.webserver

import kotlinx.coroutines.test.runTest
import okio.Path
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewState.WebServerConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WebServerConfigurationViewModelTest : AppTest() {

    private lateinit var webServerConfigurationViewModel: WebServerConfigurationViewModel

    private lateinit var initialWebServerConfigurationData: WebServerConfigurationData
    private lateinit var webServerConfigurationData: WebServerConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialWebServerConfigurationData = WebServerConfigurationData(
            isHttpServerEnabled = true,
            httpServerPort = 12101,
            isHttpServerSSLEnabled = false,
            httpServerSSLKeyStoreFile = null,
            httpServerSSLKeyStorePassword = "",
            httpServerSSLKeyAlias = "",
            httpServerSSLKeyPassword = ""
        )

        webServerConfigurationData = WebServerConfigurationData(
            isHttpServerEnabled = false,
            httpServerPort = 4569,
            isHttpServerSSLEnabled = true,
            httpServerSSLKeyStoreFile = Path.commonInternalPath(get(), getRandomString(5)),
            httpServerSSLKeyStorePassword = getRandomString(5),
            httpServerSSLKeyAlias = getRandomString(5),
            httpServerSSLKeyPassword = getRandomString(5)
        )

        webServerConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialWebServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )

        with(webServerConfigurationData) {
            webServerConfigurationViewModel.onEvent(SetHttpServerEnabled(isHttpServerEnabled))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLEnabled(isHttpServerSSLEnabled))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyAlias(httpServerSSLKeyAlias))
            webServerConfigurationViewModel.onEvent(
                UpdateHttpSSLKeyPassword(
                    httpServerSSLKeyPassword
                )
            )
            webServerConfigurationViewModel.onEvent(
                UpdateHttpSSLKeyStorePassword(
                    httpServerSSLKeyStorePassword
                )
            )
            webServerConfigurationViewModel.onEvent(UpdateHttpServerPort(httpServerPort.toString()))
            webServerConfigurationViewModel.onEvent(
                SetHttpServerSSLKeyStoreFile(
                    httpServerSSLKeyStoreFile!!
                )
            )
        }

        assertEquals(
            webServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )

        webServerConfigurationViewModel.onEvent(Save)

        assertEquals(
            webServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )
        assertEquals(webServerConfigurationData, WebServerConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialWebServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )

        with(webServerConfigurationData) {
            webServerConfigurationViewModel.onEvent(SetHttpServerEnabled(isHttpServerEnabled))
            webServerConfigurationViewModel.onEvent(SetHttpServerSSLEnabled(isHttpServerSSLEnabled))
            webServerConfigurationViewModel.onEvent(UpdateHttpSSLKeyAlias(httpServerSSLKeyAlias))
            webServerConfigurationViewModel.onEvent(
                UpdateHttpSSLKeyPassword(
                    httpServerSSLKeyPassword
                )
            )
            webServerConfigurationViewModel.onEvent(
                UpdateHttpSSLKeyStorePassword(
                    httpServerSSLKeyStorePassword
                )
            )
            webServerConfigurationViewModel.onEvent(UpdateHttpServerPort(httpServerPort.toString()))
            webServerConfigurationViewModel.onEvent(
                SetHttpServerSSLKeyStoreFile(
                    httpServerSSLKeyStoreFile!!
                )
            )
        }

        assertEquals(
            webServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )

        webServerConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialWebServerConfigurationData,
            webServerConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialWebServerConfigurationData, WebServerConfigurationData())
    }
}