package org.rhasspy.mobile.ui.content.elements

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import android.widget.TextView
import androidx.compose.runtime.Composable

/**
 * HTML text to correctly display html
 */
@Composable
actual fun HtmlText(html: String, modifier: Modifier, color: Color) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context).apply { setTextColor(color.toArgb()) } },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY) }
    )
}