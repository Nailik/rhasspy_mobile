package org.rhasspy.mobile.services.wakeword

sealed class PorcupineError(val exception: Exception?) {
    class ActivationException(exception: Exception) : PorcupineError(exception)
    class ActivationLimitException(exception: Exception) : PorcupineError(exception)
    class ActivationRefusedException(exception: Exception) : PorcupineError(exception)
    class ActivationThrottledException(exception: Exception) : PorcupineError(exception)
    class InvalidArgumentException(exception: Exception) : PorcupineError(exception)
    class InvalidStateException(exception: Exception) : PorcupineError(exception)
    class IOException(exception: Exception) : PorcupineError(exception)
    class KeyException(exception: Exception) : PorcupineError(exception)
    class MemoryException(exception: Exception) : PorcupineError(exception)
    class RuntimeException(exception: Exception) : PorcupineError(exception)
    class StopIterationException(exception: Exception) : PorcupineError(exception)
    class Other(exception: Exception) : PorcupineError(exception)
    object MicrophonePermissionMissing : PorcupineError(null)
    object NotInitialized : PorcupineError(null)
}