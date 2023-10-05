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
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.Change.SelectWakeDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData.WakeWordUdpConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WakeWordConfigurationViewModelTest : AppTest() {

    private lateinit var wakeDomainConfigurationViewModel: WakeDomainConfigurationViewModel

    private lateinit var initialWakeDomainConfigurationData: WakeDomainConfigurationData
    private lateinit var wakeDomainConfigurationData: WakeDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialWakeDomainConfigurationData = WakeDomainConfigurationData()

        wakeDomainConfigurationData = WakeDomainConfigurationData(
            wakeDomainOption = WakeDomainOption.Rhasspy2HermesMQTT,
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

        wakeDomainConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialWakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)

        with(wakeDomainConfigurationData) {
            wakeDomainConfigurationViewModel.onEvent(SelectWakeDomainOption(wakeDomainOption))

            with(wakeWordPorcupineConfigurationData) {
                wakeDomainConfigurationViewModel.onEvent(UpdateWakeDomainPorcupineAccessToken(accessToken))
                wakeDomainConfigurationViewModel.onEvent(SelectWakeDomainPorcupineLanguage(porcupineLanguage))


                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalFilePath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(SetPorcupineKeywordCustom(it.copy(sensitivity = 0.5), false))
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordCustom(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        UpdateWakeDomainPorcupineKeywordCustomSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }
                deletedCustomOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        SetPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        UpdateWakeDomainPorcupineKeywordDefaultSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }

            }

            with(wakeWordUdpConfigurationData) {
                wakeDomainConfigurationViewModel.onEvent(UpdateUdpOutputHost(outputHost))
                wakeDomainConfigurationViewModel.onEvent(UpdateUdpOutputPort(outputPort.toString()))
            }
        }

        assertEquals(wakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)

        wakeDomainConfigurationViewModel.onEvent(Save)

        assertEquals(wakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(wakeDomainConfigurationData, WakeDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialWakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)

        with(wakeDomainConfigurationData) {
            wakeDomainConfigurationViewModel.onEvent(SelectWakeDomainOption(wakeDomainOption))

            with(wakeWordPorcupineConfigurationData) {
                wakeDomainConfigurationViewModel.onEvent(UpdateWakeDomainPorcupineAccessToken(accessToken))
                wakeDomainConfigurationViewModel.onEvent(
                    SelectWakeDomainPorcupineLanguage(porcupineLanguage)
                )

                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(AddPorcupineKeywordCustom(Path.commonInternalFilePath(get(), it.fileName)))
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        SetPorcupineKeywordCustom(
                            it.copy(
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordCustom(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                customOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        UpdateWakeDomainPorcupineKeywordCustomSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }
                deletedCustomOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(DeletePorcupineKeywordCustom(it))
                }

                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        SetPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            ), false
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        ClickPorcupineKeywordDefault(
                            it.copy(
                                isEnabled = false,
                                sensitivity = 0.5
                            )
                        )
                    )
                }
                defaultOptions.forEach {
                    wakeDomainConfigurationViewModel.onEvent(
                        UpdateWakeDomainPorcupineKeywordDefaultSensitivity(
                            it.copy(sensitivity = 0.5),
                            0.7
                        )
                    )
                }

            }

            with(wakeWordUdpConfigurationData) {
                wakeDomainConfigurationViewModel.onEvent(UpdateUdpOutputHost(outputHost))
                wakeDomainConfigurationViewModel.onEvent(UpdateUdpOutputPort(outputPort.toString()))
            }
        }

        assertEquals(wakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)

        wakeDomainConfigurationViewModel.onEvent(Discard)

        assertEquals(initialWakeDomainConfigurationData, wakeDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(initialWakeDomainConfigurationData, WakeDomainConfigurationData())
    }

}