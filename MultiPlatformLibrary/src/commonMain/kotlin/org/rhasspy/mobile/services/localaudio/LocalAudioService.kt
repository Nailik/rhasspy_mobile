package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.sounds.SoundOption
import org.rhasspy.mobile.viewmodel.settings.sound.SoundFileFolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//TODO logging
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
            volume = AppSetting.volume.value,
            onFinished = {
                continuation.resume(Unit)
            },
            onError = {
                continuation.resume(Unit)
            }
        )
    }

    fun playWakeSound() {
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_hi,
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSetting.wakeSound.value}",
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun playRecordedSound() {
        when (AppSetting.recordedSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_lo,
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSetting.recordedSound.value}",
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun playErrorSound() {
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_error,
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Error}/${AppSetting.errorSound.value}",
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun stop() {
        audioPlayer.stop()
    }

}