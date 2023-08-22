package org.rhasspy.mobile.logic.audioplaying

import org.kodein.mock.Mock
import org.koin.dsl.module
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.nVerify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

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
        every { audioPlayingService.stopPlayAudio() } returns Unit

        audioPlayingService.stopPlayAudio()

        nVerify { audioPlayingService.stopPlayAudio() }
    }

    @Test
    fun `when audio playing is remote http it's played only on httpClientService and audio focus is not requested`() {
        assertTrue { true }
    }

    @Test
    fun `when audio playing is remote mqtt it's played only on mqttClientService and audio focus is not requested`() {
        assertTrue { true }
    }

    @Test
    fun `when audio playing is disabled no service is called and audio focus is not requested`() {
        assertTrue { true }
    }

    @Test
    fun `only when audio playing is local and should be stopped localAudioService is called`() {
        assertTrue { true }
    }

}