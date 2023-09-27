package org.rhasspy.mobile.logic.domains.vad

import kotlinx.datetime.Instant
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import kotlin.time.Duration

/**
 * selects silence by checking if audio volume is below a threshold (automaticSilenceDetectionAudioLevel)
 * for a certain amount of time (automaticSilenceDetectionTime)
 *
 * this will only be checked after a specific time of recording (automaticSilenceDetectionMinimumTime) has passed
 */
class SilenceDetection(
    private val automaticSilenceDetectionTime: Duration,
    private val automaticSilenceDetectionMinimumTime: Duration,
    private val automaticSilenceDetectionAudioLevel: Float,
) {

    private var silenceStartTime: Instant? = null
    private var recordingStartTime: Instant? = null

    //returns true when silence was detected
    fun onAudioChunk(chunk: MicAudioChunk): Boolean {
        //set recording start time
        val startTime = recordingStartTime ?: let {
            recordingStartTime = chunk.timeStamp
            chunk.timeStamp
        }

        //minimum recording time not reached
        if (chunk.timeStamp.minus(startTime) < automaticSilenceDetectionMinimumTime) return false

        var max: Short = 0
        for (i in 0..chunk.data.size step 2) {
            if (i < chunk.data.size) {
                val short = byteArrayToIntLittleEndian(chunk.data.copyOfRange(i, i + 2)).toShort()

                if (short > max) {
                    max = short
                }
            }
        }

        //volume above threshold
        if (max > automaticSilenceDetectionAudioLevel) {
            //reset silence time (was above threshold)
            silenceStartTime = null
            return false
        }

        //volume below threshold

        //not initial silence
        return silenceStartTime?.let { silenceStart ->
            //check if silence was detected for x milliseconds
            chunk.timeStamp.minus(silenceStart) >= automaticSilenceDetectionTime
        } ?: run {
            //first time silence was detected
            silenceStartTime = chunk.timeStamp
            false
        }

    }

    private fun byteArrayToIntLittleEndian(byteArray: ByteArray): Int {
        var result = 0
        for (i in byteArray.indices) {
            result = result or (byteArray[i].toInt() and 0xFF shl (i * 8))
        }
        return result
    }

}