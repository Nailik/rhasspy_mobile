package org.rhasspy.mobile.data.audiorecorder

import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ChecksSdkIntAtLeast
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
actual enum class AudioSourceType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption {

    Default(MR.strings.source_type_default.stable, MediaRecorder.AudioSource.DEFAULT),
    Mic(MR.strings.source_type_mic.stable, MediaRecorder.AudioSource.MIC),
    @SuppressLint("InlinedApi")
    Unprocessed(MR.strings.source_type_unprocessed.stable, MediaRecorder.AudioSource.UNPROCESSED),
    VoiceCommunication(MR.strings.source_type_voice_communication.stable, MediaRecorder.AudioSource.VOICE_COMMUNICATION),
    VoiceRecognition(MR.strings.source_type_voice_recognition.stable, MediaRecorder.AudioSource.VOICE_RECOGNITION);

    actual companion object {
        actual val default: AudioSourceType get() = Default

        actual fun supportedValues(): List<AudioSourceType> {
            return buildList {
                addAll(listOf(Default, Mic))
                if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    add(Unprocessed)
                }
                addAll(listOf(VoiceCommunication, VoiceRecognition))
            }
        }
    }

}