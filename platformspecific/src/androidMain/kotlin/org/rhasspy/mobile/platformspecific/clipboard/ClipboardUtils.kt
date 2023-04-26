package org.rhasspy.mobile.platformspecific.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import dev.icerock.moko.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication


actual object ClipboardUtils : KoinComponent {

    private val nativeApplication by inject<NativeApplication>()

    actual fun copyToClipboard(label: StringResource, text: String) {
        val clipboard: ClipboardManager = nativeApplication.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.getString(nativeApplication), text)
        clipboard.setPrimaryClip(clip)
    }

}