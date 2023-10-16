package org.rhasspy.mobile.logic.domains.snd

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.data.service.option.SndDomainOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.audiofile.AudioCollector
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.SndResult.SndError
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.logic.pipeline.domain.Reason
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.timeoutWithDefault
import org.rhasspy.mobile.settings.AppSetting

/**
 * records audio as soon as audioStream has subscribers
 */
internal interface ISndDomain : IDomain {

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    suspend fun awaitPlayAudio(audio: Audio): SndResult

    suspend fun awaitPlayAudio(audio: AudioSource)

}

/**
 * records audio as soon as audioStream has subscribers
 */
internal class SndDomain(
    private val params: SndDomainData,
    private val fileStorage: IFileStorage,
    private val audioFocusService: IAudioFocus,
    private val localAudioService: ILocalAudioPlayer,
    private val mqttConnection: IMqttConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
    private val domainHistory: IDomainHistory,
    private val indication: IIndication,
) : ISndDomain {

    private val logger = Logger.withTag("SndDomain")

    private val scope = CoroutineScope(Dispatchers.IO)

    private val audioCollector = AudioCollector()

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    override suspend fun awaitPlayAudio(audio: Audio): SndResult {
        logger.d { "awaitPlayAudio $audio" }

        return when (params.option) {
            SndDomainOption.Local              -> onLocalPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio)
            SndDomainOption.Disabled           ->
                SndError(
                    id = null,
                    sessionId = audio.sessionId,
                    reason = Reason.Disabled,
                    source = Local,
                )
        }.also {
            domainHistory.addToHistory(audio, it)
        }
    }

    override suspend fun awaitPlayAudio(audio: AudioSource) {
        when (params.option) {
            SndDomainOption.Local              -> onLocalPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio)
            SndDomainOption.Disabled           -> Unit
        }
    }

    /**
     * collect chunk stream into file and then plays audio
     */
    private suspend fun onLocalPlayAudio(audio: Audio): SndResult {
        logger.d { "onLocalPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            audioCollector.fileFromAudioFlow(fileStorage.speechToTextAudioFile, audio.data)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
            reason = Reason.Timeout,
            source = Local,
        )

        onLocalPlayAudio(data)

        return Played(
            id = null,
            sessionId = audio.sessionId,
            source = Local,
        )
    }

    private suspend fun onLocalPlayAudio(data: AudioSource) {
        audioFocusService.request(Sound)

        if (AppSetting.isAudioOutputEnabled.value) {
            indication.onPlayAudio()

            localAudioService.playAudio(
                audioSource = data,
                volume = AppSetting.volume.value,
                audioOutputOption = params.localOutputOption,
            )
        }

        audioFocusService.abandon(Sound)
    }

    /**
     * collect chunk stream into file and then plays audio, doesn't await for end
     */
    private suspend fun onRhasspy2HermesHttpPlayAudio(audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesHttpPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            audioCollector.fileFromAudioFlow(fileStorage.speechToTextAudioFile, audio.data)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
            reason = Reason.Timeout,
            source = Local,
        )

        onRhasspy2HermesHttpPlayAudio(data)

        return Played(
            id = null,
            sessionId = audio.sessionId,
            source = Rhasspy2HermesHttp,
        )
    }

    private suspend fun onRhasspy2HermesHttpPlayAudio(data: AudioSource) {
        httpClientConnection.playWav(
            audioSource = data,
        )
    }

    /**
     * collect chunk stream into file and then plays audio via mqtt, awaits for play finished
     */
    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesMQTTPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            audioCollector.fileFromAudioFlow(fileStorage.speechToTextAudioFile, audio.data)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
            reason = Reason.Timeout,
            source = Local,
        )

        val mqttRequestId = onRhasspy2HermesMQTTPlayAudio(data)

        //await played
        return mqttConnection.incomingMessages
            .filterIsInstance<PlayFinished>()
            .filter { it.id == mqttRequestId }
            .map {
                Played(
                    id = mqttRequestId,
                    sessionId = audio.sessionId,
                    source = Rhasspy2HermesMqtt,
                )
            }.timeoutWithDefault(
                timeout = params.rhasspy2HermesMqttTimeout,
                default = SndError(
                    id = mqttRequestId,
                    sessionId = audio.sessionId,
                    reason = Reason.Timeout,
                    source = Local,
                ),
            )
            .first()
    }

    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: AudioSource): String {
        val mqttRequestId = uuid4().toString()

        //play
        mqttConnection.playAudioRemote(
            audioSource = audio,
            siteId = params.mqttSiteId,
            id = mqttRequestId,
        )

        return mqttRequestId
    }



    override fun dispose() {
        scope.cancel()
        audioCollector.dispose()
    }

}