package org.rhasspy.mobile.platformspecific.toast

import android.widget.Toast
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun NativeApplication.shortToast(resource: StringResource) {
    val text = StringDesc.Resource(resource).toString(this)
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

actual fun NativeApplication.longToast(resource: StringResource) {
    val text = StringDesc.Resource(resource).toString(this)
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}