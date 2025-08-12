package org.rhasspy.mobile.platformspecific.audioplayer

import dev.icerock.moko.resources.FileResource
import okio.Path
import org.koin.core.component.KoinComponent

sealed class AudioSource : KoinComponent {
    @Deprecated("remove as soon as possible (when mqtt doesn't require it anymore)")
    class Data(val data: ByteArray) : AudioSource()
    class File(val path: Path) : AudioSource()
    class Resource(val fileResource: FileResource) : AudioSource()

    override fun toString(): String {
        return when (this) {
            is Data -> data.toString()
            is File -> path.toString()
            is Resource -> fileResource.toString()
        }
    }

}
