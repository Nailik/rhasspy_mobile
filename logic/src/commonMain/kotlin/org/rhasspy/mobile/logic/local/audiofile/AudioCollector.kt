package org.rhasspy.mobile.logic.local.audiofile

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import okio.Path
import org.rhasspy.mobile.logic.pipeline.SndAudio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

class AudioCollector {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var audioFileWriter: AudioFileWriter? = null

    /**
     * collect chunk stream into file, awaits for AudioStopEvent with timeout
     */
    suspend fun fileFromAudioFlow(path: Path, data: Flow<SndAudio>): AudioSource.File {
        //await audio start
        val audioStartEvent = data
            .filterIsInstance<SndAudio.AudioStartEvent>()
            .first()
            .also {
                //TODO domainHistory.addToHistory(audio, it)
            }

        val localAudioFileWriter = AudioFileWriter(
            path = path,
            channel = audioStartEvent.channel,
            sampleRate = audioStartEvent.sampleRate,
            bitRate = audioStartEvent.bitRate,
        ).apply {
            openFile()
        }

        audioFileWriter = localAudioFileWriter

        val collectJob = scope.launch {
            //To file
            data
                .filterIsInstance<SndAudio.AudioChunkEvent>()
                .collect {
                    localAudioFileWriter.writeToFile(it.data)
                }
        }

        //await audio stop
        data
            .filterIsInstance<SndAudio.AudioStopEvent>()
            .first()
            .also {
                //TODO   domainHistory.addToHistory(audio, it)
            }

        collectJob.cancelAndJoin()
        localAudioFileWriter.closeFile()

        return AudioSource.File(localAudioFileWriter.path)
    }

    fun dispose() {
        scope.cancel()
        audioFileWriter?.closeFile()
    }

}