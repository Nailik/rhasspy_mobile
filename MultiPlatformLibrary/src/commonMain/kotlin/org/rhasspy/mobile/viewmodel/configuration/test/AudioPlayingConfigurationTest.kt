package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.fileutils.SoundCacheFileType
import org.rhasspy.mobile.fileutils.SoundCacheFileWriterFactory
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.option.AudioPlayingOption

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

            val fileWriterWav = SoundCacheFileWriterFactory.getFileWriter(SoundCacheFileType.playTestAudio)
            fileWriterWav.writeData(FileUtils.readDataFromFile(MR.files.etc_wav_beep_hi))
            get<AudioPlayingService>().playAudio(fileWriterWav, false)
        }
    }

}