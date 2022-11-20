package org.rhasspy.mobile.services.udp

import org.rhasspy.mobile.settings.ConfigurationSettings

data class UdpServiceParams(
    val isUdpOutputEnabled: Boolean = ConfigurationSettings.isUdpOutputEnabled.value,
    val udpOutputHost: String = ConfigurationSettings.udpOutputHost.value,
    val udpOutputPort: Int = ConfigurationSettings.udpOutputPort.value
)