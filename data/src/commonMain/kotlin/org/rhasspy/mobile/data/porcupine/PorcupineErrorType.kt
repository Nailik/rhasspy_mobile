package org.rhasspy.mobile.data.porcupine

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState

enum class PorcupineErrorType(val serviceState: ServiceState) {
    ActivationException(ServiceState.Error(MR.strings.activation_exception.stable)),
    ActivationLimitException(ServiceState.Error(MR.strings.activation_limit_exception.stable)),
    ActivationRefusedException(ServiceState.Error(MR.strings.activation_refused_exception.stable)),
    ActivationThrottledException(ServiceState.Error(MR.strings.activation_throttled_exception.stable)),
    InvalidArgumentException(ServiceState.Error(MR.strings.invalid_argument_exception.stable)),
    InvalidStateException(ServiceState.Error(MR.strings.invalid_state_exception.stable)),
    IOException(ServiceState.Error(MR.strings.io_exception.stable)),
    KeyException(ServiceState.Error(MR.strings.key_exception.stable)),
    MemoryException(ServiceState.Error(MR.strings.memory_exception.stable)),
    RuntimeException(ServiceState.Error(MR.strings.runtime_exception.stable)),
    StopIterationException(ServiceState.Error(MR.strings.stop_iteration_exception.stable)),
    Other(ServiceState.Exception()),
    MicrophonePermissionMissing(ServiceState.Error(MR.strings.microphone_permission_missing.stable)),
    NotInitialized(ServiceState.Exception());
}