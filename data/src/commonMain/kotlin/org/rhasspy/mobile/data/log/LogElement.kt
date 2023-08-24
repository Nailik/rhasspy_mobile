package org.rhasspy.mobile.data.log

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class LogElement(
    val id: Long,
    val time: String,
    val severity: Severity,
    val tag: String,
    val message: String,
    val throwable: String?
)