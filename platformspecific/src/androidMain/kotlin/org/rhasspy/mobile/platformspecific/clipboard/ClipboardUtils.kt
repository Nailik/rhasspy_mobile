package org.rhasspy.mobile.platformspecific.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication


actual object ClipboardUtils : KoinComponent {

    private val nativeApplication by inject<NativeApplication>()
    private val logger = Logger.withTag("ClipboardUtils")

    actual fun copyToClipboard(label: StringResource, text: String) {
        nativeApplication.getSystemService<ClipboardManager>()?.also { clipboardManager ->
            val clip = ClipData.newPlainText(label.getString(nativeApplication), text)
            clipboardManager.setPrimaryClip(clip)
            logger.e { "copyToClipboard clipboardManager is null" }
        }
    }

}