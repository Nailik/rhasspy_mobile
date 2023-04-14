package org.rhasspy.mobile.viewmodel.configuration.wakeword

import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword

data class PorcupineCustomKeywordUi(
    val keyword: PorcupineCustomKeyword,
    val deleted: Boolean = false
)