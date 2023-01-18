package org.rhasspy.mobile.android

import android.os.Build
import android.provider.Settings
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.permission.PermissionRequester
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel


fun SemanticsNodeInteraction.onSwitch(): SemanticsNodeInteraction {
    return this.onChildren().filter(isToggleable()).onFirst()
}

fun hasTestTag(testTag: Enum<*>): SemanticsMatcher =
    SemanticsMatcher.expectValue(SemanticsProperties.TestTag, testTag.name)

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    testTag: Enum<*>,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(testTag.name), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    name: String,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(name), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithCombinedTag(
    tag1: TestTag, tag2: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag("${tag1.name}${tag2.name}"), useUnmergedTree)


fun SemanticsNodeInteractionsProvider.onNodeWithCombinedTag(
    testTag: Enum<*>, tag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag("${testTag.name}${tag.name}"), useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithCombinedTag(
    name: String, tag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag("$name${tag.name}"), useUnmergedTree)

fun UiSelector.text(text: StringResource): UiSelector {
    return this.textMatches(
        StringDesc.Resource(text).toString(
            getInstrumentation()
                .targetContext.applicationContext
        )
    )
}


//https://github.com/SergKhram/allure-kotlin/blob/081c7d39ee440b82ca490ce91d34ce1a1421670c/allure-kotlin-android/src/main/kotlin/io/qameta/allure/android/internal/TestUtils.kt
fun requestExternalStoragePermissions(device: UiDevice) {
    when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> {
            with(PermissionRequester()) {
                addPermissions("android.permission.WRITE_EXTERNAL_STORAGE")
                addPermissions("android.permission.READ_EXTERNAL_STORAGE")
                requestPermissions()
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            device.executeShellCommand("appops set --uid ${getInstrumentation().targetContext.packageName} MANAGE_EXTERNAL_STORAGE allow")
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

fun UiDevice.requestOverlayPermissions() {
    try {
        with(PermissionRequester()) {
            try {
                addPermissions("android.permission.SYSTEM_ALERT_WINDOW")
                requestPermissions()
            } catch (e: Exception) {
                requestOverlayPermissionLegacy()
            }
        }
    } catch (e: Exception) {
        requestOverlayPermissionLegacy()
    }
    if (!Settings.canDrawOverlays(Application.nativeInstance)) {
        //will be called on android M (23)
        requestOverlayPermissionLegacy()
    }
}




fun SemanticsNodeInteraction.assertTextEquals(
    text: StringResource,
    includeEditableText: Boolean = true
): SemanticsNodeInteraction =
    this.assertTextEquals(
        values = arrayOf(
            StringDesc.Resource(text).toString(
                getInstrumentation()
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
            getInstrumentation()
                .targetContext.applicationContext
        ), substring, ignoreCase
    ), useUnmergedTree
)

fun ComposeContentTestRule.awaitSaved(viewModel: IConfigurationViewModel) {
    this.waitUntil(
        condition = { !viewModel.isLoading.value },
        timeoutMillis = 5000
    )
}