package org.rhasspy.mobile.services.hotword

enum class HotWordServiceError {
    MicrophonePermissionMissing,
    PorcupineActivationException,
    PorcupineActivationLimitException,
    PorcupineActivationRefusedException,
    PorcupineActivationThrottledException,
    PorcupineInvalidArgumentException,
    PorcupineInvalidStateException,
    PorcupineIOException,
    PorcupineKeyException,
    PorcupineMemoryException,
    PorcupineRuntimeException,
    PorcupineStopIterationException,
    Unknown
}