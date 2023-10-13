package org.rhasspy.mobile.logic.domains.audio

import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

internal interface IAudioDomain : IDomain {

    suspend fun awaitPlayAudio(): AudioSource

}

internal class AudioDomain(
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val userConnection: IUserConnection,
    private val fileStorage: IFileStorage,
) : IAudioDomain {
    override suspend fun awaitPlayAudio(): AudioSource {
        return merge(
            //Mqtt: PlayBytes
            mqttConnection.incomingMessages
                .filterIsInstance<MqttConnectionEvent.PlayResult.PlayBytes>()
                .map {
                    AudioSource.Data(it.byteArray)
                },
            //WebServer: WebServerPlayWav, WebServerSay
            webServerConnection.incomingMessages
                .filterIsInstance<WebServerConnectionEvent.WebServerPlayWav>()
                .map {
                    AudioSource.Data(it.data)
                },
            //Local: PlayRecording
            userConnection.incomingMessages
                .filterIsInstance<UserConnectionEvent.StartStopPlayRecording>()
                .map {
                    AudioSource.File(fileStorage.speechToTextAudioFile)
                },
        ).first()
    }

    override fun dispose() {}

}