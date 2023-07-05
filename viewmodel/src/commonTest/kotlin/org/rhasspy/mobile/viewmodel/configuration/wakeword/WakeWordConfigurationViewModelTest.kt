package org.rhasspy.mobile.viewmodel.configuration.wakeword

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import okio.Path
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordUdpConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WakeWordConfigurationViewModelTest : AppTest() {

    private lateinit var wakeWordConfigurationViewModel: WakeWordConfigurationViewModel

    private lateinit var initialWakeWordConfigurationData: WakeWordConfigurationData
    private lateinit var wakeWordConfigurationData: WakeWordConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialWakeWordConfigurationData = WakeWordConfigurationData(
            wakeWordOption = WakeWordOption.Disabled,
            wakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(
                accessToken = "",
                porcupineLanguage = PorcupineLanguageOption.EN,
                defaultOptions = PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }.toImmutableList(),
                customOptions = persistentListOf(),
                deletedCustomOptions = persistentListOf()
            ),
            wakeWordUdpConfigurationData = WakeWordUdpConfigurationData(
                outputHost = "",
                outputPort = 20000
            )
        )

        wakeWordConfigurationData = WakeWordConfigurationData(
            wakeWordOption = WakeWordOption.MQTT,
            wakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(
                accessToken = getRandomString(5),
                porcupineLanguage = PorcupineLanguageOption.DE,
                defaultOptions = PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, true, 0.7f) }.toImmutableList(),
                customOptions = persistentListOf(PorcupineCustomKeyword(fileName = getRandomString(5), isEnabled = true, sensitivity = 0.7f)),
                deletedCustomOptions = persistentListOf()
            ),
            wakeWordUdpConfigurationData = WakeWordUdpConfigurationData(
                outputHost = getRandomString(5),
                outputPort = 2344
            )
        )

        wakeWordConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialWakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)

        with(wakeWordConfigurationData) {
            wakeWordConfigurationViewModel.onEvent(SelectWakeWordOption(wakeWordOption))

            with(wakeWordPorcupineConfigurationData) {
                wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineAccessToken(accessToken))
                wakeWordConfigurationViewModel.onEvent(SelectWakeWordPorcupineLanguage(porcupineLanguage))


                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalPath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(SetPorcupineKeywordCustom(it.copy(sensitivity = 0.5f), false))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(ClickPorcupineKeywordCustom(it.copy(isEnabled = false, sensitivity = 0.5f)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineKeywordCustomSensitivity(it.copy(sensitivity = 0.5f), 0.7f))
                }
                deletedCustomOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(SetPorcupineKeywordDefault(it.copy(isEnabled = false, sensitivity = 0.5f), false))
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(ClickPorcupineKeywordDefault(it.copy(isEnabled = false, sensitivity = 0.5f)))
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineKeywordDefaultSensitivity(it.copy(sensitivity = 0.5f), 0.7f))
                }

            }

            with(wakeWordUdpConfigurationData) {
                wakeWordConfigurationViewModel.onEvent(UpdateUdpOutputHost(outputHost))
                wakeWordConfigurationViewModel.onEvent(UpdateUdpOutputPort(outputPort.toString()))
            }
        }

        assertEquals(wakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)

        wakeWordConfigurationViewModel.onEvent(Save)

        assertEquals(wakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)
        assertEquals(wakeWordConfigurationData, WakeWordConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialWakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)

        with(wakeWordConfigurationData) {
            wakeWordConfigurationViewModel.onEvent(SelectWakeWordOption(wakeWordOption))

            with(wakeWordPorcupineConfigurationData) {
                wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineAccessToken(accessToken))
                wakeWordConfigurationViewModel.onEvent(SelectWakeWordPorcupineLanguage(porcupineLanguage))


                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalPath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(SetPorcupineKeywordCustom(it.copy(sensitivity = 0.5f), false))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(ClickPorcupineKeywordCustom(it.copy(isEnabled = false, sensitivity = 0.5f)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineKeywordCustomSensitivity(it.copy(sensitivity = 0.5f), 0.7f))
                }
                deletedCustomOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(SetPorcupineKeywordDefault(it.copy(isEnabled = false, sensitivity = 0.5f), false))
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(ClickPorcupineKeywordDefault(it.copy(isEnabled = false, sensitivity = 0.5f)))
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(UpdateWakeWordPorcupineKeywordDefaultSensitivity(it.copy(sensitivity = 0.5f), 0.7f))
                }

            }

            with(wakeWordUdpConfigurationData) {
                wakeWordConfigurationViewModel.onEvent(UpdateUdpOutputHost(outputHost))
                wakeWordConfigurationViewModel.onEvent(UpdateUdpOutputPort(outputPort.toString()))
            }
        }

        assertEquals(wakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)

        wakeWordConfigurationViewModel.onEvent(Discard)

        assertEquals(initialWakeWordConfigurationData, wakeWordConfigurationViewModel.viewState.value.editData)
        assertEquals(initialWakeWordConfigurationData, WakeWordConfigurationData())
    }
}

//TODO deleted list gets really deleted (files)
//TODO saved files are copied