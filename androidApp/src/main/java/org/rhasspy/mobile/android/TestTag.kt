package org.rhasspy.mobile.android

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

enum class TestTag {
    DialogInformationMicrophonePermission,
    DialogOk,
    DialogCancel

}

fun Modifier.testTag(tag: TestTag) = semantics(
    properties = {
        testTag = tag.name
    }
)