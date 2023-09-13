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
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordUdpConfigurationData
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

        initialWakeWordConfigurationData = WakeWordConfigurationData()

        wakeWordConfigurationData = WakeWordConfigurationData(
            wakeWordOption = WakeWordOption.Rhasspy2HermesMQTT,
            wakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(
                accessToken = getRandomString(5),
                porcupineLanguage = PorcupineLanguageOption.DE,
                defaultOptions = PorcupineKeywordOption.entries
                    .map { PorcupineDefaultKeyword(it, true, 0.7) }.toImmutableList(), //sort is necessary because database also sorts
                customOptions = persistentListOf(
                    PorcupineCustomKeyword(
                        fileName = getRandomString(5),
                        isEnabled = true,
                        sensitivity = 0.7
                    )
                ),
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
                    wakeWordConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalFilePath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(SetPorcupineKeywordCustom(it.copy(sensitivity = 0.5), false))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordCustom(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        UpdateWakeWordPorcupineKeywordCustomSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }
                deletedCustomOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        SetPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        UpdateWakeWordPorcupineKeywordDefaultSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
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
                wakeWordConfigurationViewModel.onEvent(
                    SelectWakeWordPorcupineLanguage(porcupineLanguage)
                )

                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalFilePath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        SetPorcupineKeywordCustom(
                            it.copy(
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordCustom(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                customOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        UpdateWakeWordPorcupineKeywordCustomSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }
                deletedCustomOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        SetPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeWordConfigurationViewModel.onEvent(
                        UpdateWakeWordPorcupineKeywordDefaultSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
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