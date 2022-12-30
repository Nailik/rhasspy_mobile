package org.rhasspy.mobile.services.localaudio

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundOptions
import org.rhasspy.mobile.viewModels.settings.sound.SoundFileFolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocalAudioService : IService() {

    private val audioPlayer = AudioPlayer()
    val isPlayingState = audioPlayer.isPlayingState
    override fun onClose() {
        audioPlayer.stop()
        audioPlayer.close()
    }

    suspend fun playAudio(data: List<Byte>): Unit = suspendCoroutine { continuation ->
        audioPlayer.playData(
            data = data,
            onFinished = {
                continuation.resume(Unit)
            },
            onError = {
                continuation.resume(Unit)
            }
        )
    }

    fun playWakeSound() {
        when (AppSettings.wakeSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_hi,
                AppSettings.wakeSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSettings.wakeSound.value}",
                AppSettings.wakeSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }

    fun playRecordedSound() {
        when (AppSettings.recordedSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_lo,
                AppSettings.recordedSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSettings.recordedSound.value}",
                AppSettings.recordedSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }

    fun playErrorSound() {
        when (AppSettings.errorSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_error,
                AppSettings.errorSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Error}/${AppSettings.errorSound.value}",
                AppSettings.errorSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }

    fun stop() {
        audioPlayer.stop()
    }

}