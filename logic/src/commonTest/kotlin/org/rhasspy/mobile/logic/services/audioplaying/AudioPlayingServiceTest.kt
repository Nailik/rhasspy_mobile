package org.rhasspy.mobile.logic.services.audioplaying

import org.kodein.mock.Mock
import org.kodein.mock.UsesMocks
import org.koin.dsl.module
import org.rhasspy.mobile.logic.services.AppTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@UsesMocks(IAudioPlayingService::class)
class AudioPlayingServiceTest : AppTest() {

    @Mock
    lateinit var audioPlayingService: IAudioPlayingService

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { audioPlayingService }
            }
        )
    }

    @Test
    fun `when audio playing is local focus is requested and abandoned and audio is played only on local service`() {
        audioPlayingService.stopPlayAudio()
        verify { audioPlayingService.stopPlayAudio() }
    }

    @Test
    fun `when audio playing is remote http it's played only on httpClientService and audio focus is not requested`() {

    }

    fun `when audio playing is remote mqtt it's played only on mqttClientService and audio focus is not requested`() {

    }

    fun `when audio playing is disabled no service is called and audio focus is not requested`() {

    }

    @Test
    fun `only when audio playing is local and should be stopped localAudioService is called`() {

    }

}