package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import org.rhasspy.mobile.Application

actual fun openLink(link: String) {
    Application.Instance.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    })
}