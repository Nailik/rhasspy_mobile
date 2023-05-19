package org.rhasspy.mobile.viewmodel

import org.rhasspy.mobile.data.resource.StableStringResource

data class KViewState(
    val isShowMicrophonePermissionInformationDialog: Boolean,
    val microphonePermissionSnackBarText: StableStringResource?,
    val microphonePermissionSnackBarLabel: StableStringResource?
) {
}