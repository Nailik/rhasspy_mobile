package org.rhasspy.mobile.data.log

import co.touchlab.kermit.Severity
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption


enum class LogLevel(override val text: StableStringResource, val severity: Severity) :
    IOption<LogLevel> {
    Verbose(MR.strings.verbose.stable, Severity.Verbose),
    Debug(MR.strings.debug.stable, Severity.Debug),
    Info(MR.strings.info.stable, Severity.Info),
    Warn(MR.strings.warn.stable, Severity.Warn),
    Error(MR.strings.error.stable, Severity.Error),
    Assert(MR.strings.assert_level.stable, Severity.Assert);

    override fun findValue(value: String): LogLevel {
        return valueOf(value)
    }
}