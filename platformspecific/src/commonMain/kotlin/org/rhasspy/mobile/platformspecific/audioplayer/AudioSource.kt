package org.rhasspy.mobile.platformspecific.audioplayer

import dev.icerock.moko.resources.FileResource
import okio.Path
import okio.buffer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.extensions.commonSource

sealed interface AudioSource : KoinComponent {
    @Deprecated("remove as soon as possible (when mqtt doesn't require it anymore)")
    class Data(val data: ByteArray) : AudioSource
    class File(val path: Path) : AudioSource
    class Resource(val fileResource: FileResource) : AudioSource

    fun getByteData(): ByteArray = when (this) {
        is Data -> data
        is File -> path.commonSource().buffer().readByteArray()
        is Resource -> fileResource.commonData(get())
    }
}