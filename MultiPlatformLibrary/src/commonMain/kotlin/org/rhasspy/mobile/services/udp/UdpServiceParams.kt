package org.rhasspy.mobile.services.udp

import org.rhasspy.mobile.settings.ConfigurationSetting

data class UdpServiceParams(
    val udpOutputHost: String = ConfigurationSetting.udpOutputHost.value,
    val udpOutputPort: Int = ConfigurationSetting.udpOutputPort.value
)