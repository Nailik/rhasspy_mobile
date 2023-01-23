package org.rhasspy.mobile.logic.services.wakeword

data class PorcupineError(val exception: Exception?, val errorType: PorcupineErrorType)