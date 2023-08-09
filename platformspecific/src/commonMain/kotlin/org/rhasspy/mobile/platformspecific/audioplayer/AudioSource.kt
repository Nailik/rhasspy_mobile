package org.rhasspy.mobile.platformspecific.audioplayer

import dev.icerock.moko.resources.FileResource
import kotlinx.io.files.Path
import org.koin.core.component.KoinComponent

sealed class AudioSource : KoinComponent {

    class File(val path: Path) : AudioSource()
    class Resource(val fileResource: FileResource) : AudioSource()

    override fun toString(): String {
        return when (this) {
            is File     -> path.toString()
            is Resource -> fileResource.toString()
        }
    }

}
