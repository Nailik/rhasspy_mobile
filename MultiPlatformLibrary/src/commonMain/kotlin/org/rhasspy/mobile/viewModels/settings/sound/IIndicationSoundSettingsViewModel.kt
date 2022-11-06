package org.rhasspy.mobile.viewModels.settings.sound

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.sounds.SoundFile

interface IIndicationSoundSettingsViewModel {


    val isSoundIndicationDefault: StateFlow<Boolean>
    val isSoundIndicationDisabled: StateFlow<Boolean>

    val customSoundFiles: StateFlow<List<SoundFile>>
    val soundVolume: StateFlow<Float>

    fun onClickSoundIndicationDefault()

    fun onClickSoundIndicationDisabled()

    //update sound volume
    fun updateSoundVolume(volume: Float)

    //select sound file
    fun selectSoundFile(file: SoundFile)

    //delete sound file
    fun deleteSoundFile(file: SoundFile)

    //play sound file
    fun playSoundFile()

    //choose sound file from files
    fun chooseSoundFile()

}