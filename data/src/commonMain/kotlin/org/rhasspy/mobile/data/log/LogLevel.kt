package org.rhasspy.mobile.data.log

import co.touchlab.kermit.Severity
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
enum class LogLevel(override val text: StableStringResource, val severity: Severity) : IOption {

    Verbose(MR.strings.verbose.stable, Severity.Verbose),
    Debug(MR.strings.debug.stable, Severity.Debug),
    Info(MR.strings.info.stable, Severity.Info),
    Warn(MR.strings.warn.stable, Severity.Warn),
    Error(MR.strings.error.stable, Severity.Error),
    Assert(MR.strings.assert_level.stable, Severity.Assert);

}