package org.rhasspy.mobile.services.wakeword

data class PorcupineError(val exception: Exception?, val errorType: PorcupineErrorType)