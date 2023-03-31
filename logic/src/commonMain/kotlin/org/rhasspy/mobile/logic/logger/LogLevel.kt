package org.rhasspy.mobile.logic.logger

import co.touchlab.kermit.Severity
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.service.option.IOption


enum class LogLevel(override val text: StringResource, val severity: Severity) :
    IOption<LogLevel> {
    Verbose(MR.strings.verbose, Severity.Verbose),
    Debug(MR.strings.debug, Severity.Debug),
    Info(MR.strings.info, Severity.Info),
    Warn(MR.strings.warn, Severity.Warn),
    Error(MR.strings.error, Severity.Error),
    Assert(MR.strings.assert_level, Severity.Assert);

    override fun findValue(value: String): LogLevel {
        return valueOf(value)
    }
}