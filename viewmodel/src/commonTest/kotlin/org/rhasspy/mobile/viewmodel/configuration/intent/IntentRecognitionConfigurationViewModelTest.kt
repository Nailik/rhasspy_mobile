package org.rhasspy.mobile.viewmodel.configuration.intent

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationUiEvent.Change.SelectIntentDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewState.IntentDomainConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntentRecognitionConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var intentDomainConfigurationViewModel: IntentDomainConfigurationViewModel

    private lateinit var initialIntentHandlingConfigurationData: IntentDomainConfigurationData
    private lateinit var intentHandlingConfigurationData: IntentDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        initialIntentHandlingConfigurationData = IntentDomainConfigurationData(intentDomainOption = IntentDomainOption.Disabled)

        intentHandlingConfigurationData = IntentDomainConfigurationData(intentDomainOption = IntentDomainOption.Rhasspy2HermesMQTT)

        intentDomainConfigurationViewModel = get()
    }


    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialIntentHandlingConfigurationData, intentDomainConfigurationViewModel.viewState.value.editData)

        with(intentHandlingConfigurationData) {
            intentDomainConfigurationViewModel.onEvent(SelectIntentDomainOption(intentDomainOption))
        }

        assertEquals(intentHandlingConfigurationData, intentDomainConfigurationViewModel.viewState.value.editData)

        intentDomainConfigurationViewModel.onEvent(Save)

        assertEquals(intentHandlingConfigurationData, intentDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(intentHandlingConfigurationData, IntentDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialIntentHandlingConfigurationData,
            intentDomainConfigurationViewModel.viewState.value.editData
        )

        with(intentHandlingConfigurationData) {
            intentDomainConfigurationViewModel.onEvent(SelectIntentDomainOption(intentDomainOption))
        }

        assertEquals(intentHandlingConfigurationData, intentDomainConfigurationViewModel.viewState.value.editData)

        intentDomainConfigurationViewModel.onEvent(Discard)

        assertEquals(initialIntentHandlingConfigurationData, intentDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(initialIntentHandlingConfigurationData, IntentDomainConfigurationData())
    }
}