package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.nativeutils.FileUtils
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.settings.option.AudioPlayingOption

class AudioPlayingConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<AudioPlayingService>().serviceState

    fun playTestAudio() {
        testScope.launch {
            if (get<AudioPlayingServiceParams>().audioPlayingOption == AudioPlayingOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }
            get<AudioPlayingService>().playAudio(FileUtils.readDataFromFile(MR.files.etc_wav_beep_hi), false)
        }
    }

}