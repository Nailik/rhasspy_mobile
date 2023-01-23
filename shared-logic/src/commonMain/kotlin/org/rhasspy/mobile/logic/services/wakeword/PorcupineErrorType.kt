package org.rhasspy.mobile.logic.services.wakeword

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.middleware.ServiceState

enum class PorcupineErrorType(val serviceState: ServiceState) {
    ActivationException(ServiceState.Error(MR.strings.activation_exception)),
    ActivationLimitException(ServiceState.Error(MR.strings.activation_limit_exception)),
    ActivationRefusedException(ServiceState.Error(MR.strings.activation_refused_exception)),
    ActivationThrottledException(ServiceState.Error(MR.strings.activation_throttled_exception)),
    InvalidArgumentException(ServiceState.Error(MR.strings.invalid_argument_exception)),
    InvalidStateException(ServiceState.Error(MR.strings.invalid_state_exception)),
    IOException(ServiceState.Error(MR.strings.io_exception)),
    KeyException(ServiceState.Error(MR.strings.key_exception)),
    MemoryException(ServiceState.Error(MR.strings.memory_exception)),
    RuntimeException(ServiceState.Error(MR.strings.runtime_exception)),
    StopIterationException(ServiceState.Error(MR.strings.stop_iteration_exception)),
    Other(ServiceState.Exception()),
    MicrophonePermissionMissing(ServiceState.Error(MR.strings.microphone_permission_missing)),
    NotInitialized(ServiceState.Exception());
}