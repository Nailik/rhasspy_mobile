package org.rhasspy.mobile.data.log

import co.touchlab.kermit.Severity
import kotlinx.serialization.Serializable

@Serializable
data class LogElement(
    val time: String,
    val severity: Severity,
    val tag: String,
    val message: String,
    val throwable: String?
)