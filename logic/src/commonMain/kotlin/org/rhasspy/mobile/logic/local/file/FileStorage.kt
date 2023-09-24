package org.rhasspy.mobile.logic.local.file

import okio.Path
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath

interface IFileStorage {

    val speechToTextAudioFile: Path

    val playAudioFile: Path

}

internal class FilesStorage(nativeApplication: NativeApplication) : IFileStorage {

    override val speechToTextAudioFile: Path = Path.commonInternalFilePath(nativeApplication, "SpeechToTextAudio.wav")
    override val playAudioFile: Path = Path.commonInternalFilePath(nativeApplication, "PlayAudio.wav")

}