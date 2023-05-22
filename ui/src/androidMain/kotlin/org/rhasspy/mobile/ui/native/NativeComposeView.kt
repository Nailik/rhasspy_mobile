package org.rhasspy.mobile.ui.native

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

fun nativeComposeView(context: Context, content: @Composable (view: View) -> Unit): ComposeView {
    return ComposeView(context).apply {
        setContent {
            content(this)
        }
    }
}