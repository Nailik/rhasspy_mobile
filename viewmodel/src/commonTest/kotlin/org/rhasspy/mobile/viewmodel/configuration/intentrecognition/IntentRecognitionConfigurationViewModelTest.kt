package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntentRecognitionConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var intentRecognitionConfigurationViewModel: IntentRecognitionConfigurationViewModel

    private lateinit var initialIntentHandlingConfigurationData: IntentRecognitionConfigurationData
    private lateinit var intentHandlingConfigurationData: IntentRecognitionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        initialIntentHandlingConfigurationData = IntentRecognitionConfigurationData(
            intentRecognitionOption = IntentRecognitionOption.Disabled,
            isUseCustomIntentRecognitionHttpEndpoint = false,
            intentRecognitionHttpEndpoint = ""
        )

        intentHandlingConfigurationData = IntentRecognitionConfigurationData(
            intentRecognitionOption = IntentRecognitionOption.RemoteMQTT,
            isUseCustomIntentRecognitionHttpEndpoint = true,
            intentRecognitionHttpEndpoint = getRandomString(5)
        )

        intentRecognitionConfigurationViewModel = get()
    }


    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialIntentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)

        with(intentHandlingConfigurationData) {
            intentRecognitionConfigurationViewModel.onEvent(ChangeIntentRecognitionHttpEndpoint(intentRecognitionHttpEndpoint))
            intentRecognitionConfigurationViewModel.onEvent(SelectIntentRecognitionOption(intentRecognitionOption))
            intentRecognitionConfigurationViewModel.onEvent(SetUseCustomHttpEndpoint(isUseCustomIntentRecognitionHttpEndpoint))
        }

        assertEquals(intentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)

        intentRecognitionConfigurationViewModel.onEvent(Save)

        assertEquals(intentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)
        assertEquals(intentHandlingConfigurationData, IntentRecognitionConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialIntentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)

        with(intentHandlingConfigurationData) {
            intentRecognitionConfigurationViewModel.onEvent(ChangeIntentRecognitionHttpEndpoint(intentRecognitionHttpEndpoint))
            intentRecognitionConfigurationViewModel.onEvent(SelectIntentRecognitionOption(intentRecognitionOption))
            intentRecognitionConfigurationViewModel.onEvent(SetUseCustomHttpEndpoint(isUseCustomIntentRecognitionHttpEndpoint))
        }

        assertEquals(intentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)

        intentRecognitionConfigurationViewModel.onEvent(Discard)

        assertEquals(initialIntentHandlingConfigurationData, intentRecognitionConfigurationViewModel.viewState.value.editData)
        assertEquals(initialIntentHandlingConfigurationData, IntentRecognitionConfigurationData())
    }
}