package org.rhasspy.mobile.android

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.permission.PermissionRequester
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlin.test.assertEquals

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    testTag: Enum<*>,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(testTag.name), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    name: String,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(name), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithCombinedTag(
    testTag: Enum<*>, tag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag("${testTag.name}${tag.name}"), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithCombinedTag(
    name: String, tag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag("$name${tag.name}"), useUnmergedTree)

fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
    val capturedName = captureToImage().colorSpace.name
    assertEquals(expectedBackground.colorSpace.name, capturedName)
}

fun UiSelector.text(text: StringResource): UiSelector {
    return this.textMatches(
        StringDesc.Resource(text).toString(
            InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext
        )
    )
}


//https://github.com/SergKhram/allure-kotlin/blob/081c7d39ee440b82ca490ce91d34ce1a1421670c/allure-kotlin-android/src/main/kotlin/io/qameta/allure/android/internal/TestUtils.kt
fun requestExternalStoragePermissions(device: UiDevice) {
    when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
            with(PermissionRequester()) {
                addPermissions("android.permission.WRITE_EXTERNAL_STORAGE")
                addPermissions("android.permission.READ_EXTERNAL_STORAGE")
                requestPermissions()
            }
        }
        Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
            device.executeShellCommand("appops set --uid ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} LEGACY_STORAGE allow")
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            device.executeShellCommand("appops set --uid ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} MANAGE_EXTERNAL_STORAGE allow")
        }
        else -> return
    }
}

fun requestMicrophonePermissions() {
    with(PermissionRequester()) {
        addPermissions("android.permission.RECORD_AUDIO")
        requestPermissions()
    }
}


fun SemanticsNodeInteraction.assertTextEquals(
    text: StringResource,
    includeEditableText: Boolean = true
): SemanticsNodeInteraction =
    this.assertTextEquals(
        values = arrayOf(
            StringDesc.Resource(text).toString(
                InstrumentationRegistry.getInstrumentation()
                    .targetContext.applicationContext
            )
        ),
        includeEditableText = includeEditableText
    )

fun SemanticsNodeInteractionsProvider.onNodeWithText(
    text: StringResource,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(
    hasText(
        StringDesc.Resource(text).toString(
            InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext
        ), substring, ignoreCase
    ), useUnmergedTree
)