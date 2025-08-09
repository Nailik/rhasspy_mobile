package org.rhasspy.mobile.logic.services.speechtotext

import org.rhasspy.mobile.settings.AppSetting
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SilenceDetection(
    private val onSilenceDetected: () -> Unit,
) {

    private var silenceStartTime: Instant? = null
    private var recordingTillSilenceStartTime = Clock.System.now()

    fun reset() {
        silenceStartTime = null
        recordingTillSilenceStartTime = Clock.System.now()
    }

    fun audioFrameVolume(volume: Float) {
        val automaticSilenceDetectionTime = AppSetting.automaticSilenceDetectionTime.value ?: 0
        val automaticSilenceDetectionMinimumTime =
            AppSetting.automaticSilenceDetectionMinimumTime.value ?: 0
        //check enabled
        if (!AppSetting.isAutomaticSilenceDetectionEnabled.value) return

        //check minimum recording time
        val currentTime = Clock.System.now()
        //if recordingTillSilenceStartTime is null there is an issue therefore timeSinceStart is set to 0
        val timeSinceStart = currentTime.minus(recordingTillSilenceStartTime)

        //minimum recording time not reached
        if (timeSinceStart < automaticSilenceDetectionMinimumTime.milliseconds) return

        //volume above threshold
        if (volume > AppSetting.automaticSilenceDetectionAudioLevel.value) {
            //reset silence time (was above threshold)
            silenceStartTime = null
            return
        }

        //volume below threshold

        //not initial silence
        silenceStartTime?.also { silenceStart ->
            //silence duration
            val timeSinceSilenceDetected = currentTime.minus(silenceStart)
            //check if silence was detected for x milliseconds
            if (timeSinceSilenceDetected >= automaticSilenceDetectionTime.milliseconds) {
                onSilenceDetected()
            }
        } ?: run {
            //first time silence was detected
            silenceStartTime = Clock.System.now()
        }

    }

}