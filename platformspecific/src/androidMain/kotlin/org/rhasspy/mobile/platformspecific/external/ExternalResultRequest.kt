package org.rhasspy.mobile.platformspecific.external

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.*
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.*
import kotlin.coroutines.resume

actual object ExternalResultRequest : KoinComponent {

    private val logger = Logger.withTag("ExternalRedirect")
    private val nativeApplication by inject<NativeApplication>()

    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var somePermissionResultLauncher: ActivityResultLauncher<String>

    private var activityResultCallback: ((activityResult: ActivityResult) -> Unit)? = null
    private var permissionResultCallback: ((granted: Boolean) -> Unit)? = null

    fun registerCallback(activity: AppCompatActivity) {
        logger.v { "registerCallback" }
        someActivityResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            activityResultCallback?.invoke(it)
        }
        somePermissionResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            permissionResultCallback?.invoke(it)
        }
    }

    actual fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        logger.v { "launch $intention" }
        return launching {
            nativeApplication.currentActivity?.also {
                it.startActivity(intentFromIntention(intention))
            } ?: run {
                nativeApplication.startActivity(
                    intentFromIntention(intention).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> =
        suspendCancellableCoroutine { continuation ->
            logger.v { "launchForResult $intention" }
            launching<R> {
                activityResultCallback = { continuation.resume(getResult(it) as ExternalRedirectResult<R>) { cause -> Error<R>(cause) } }
                someActivityResultLauncher.launch(intentFromIntention(intention))
            }
        }

    actual suspend fun launchForPermission(permission: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            logger.v { "launchForResult $permission" }
            permissionResultCallback = { continuation.resume(it) }
            somePermissionResultLauncher.launch(permission)
        }

    private fun <R> launching(launch: () -> Unit): ExternalRedirectResult<R> {
        return try {
            launch()
            Success()
        } catch (exception: ActivityNotFoundException) {
            logger.e(exception) { "launching" }
            NotFound()
        } catch (exception: Exception) {
            logger.e(exception) { "launching" }
            Error(exception)
        }
    }

    private fun getResult(activityResult: ActivityResult): ExternalRedirectResult<String> {
        return if (activityResult.resultCode == Activity.RESULT_OK) {
            return activityResult.data?.data?.let { uri ->
                Result(uri.toString())
            } ?: Error()
        } else Error()
    }

    private fun <R> intentFromIntention(intention: ExternalResultRequestIntention<R>): Intent {
        return when (intention) {
            is CreateDocument ->
                ActivityResultContracts
                    .CreateDocument(intention.mimeType)
                    .createIntent(nativeApplication, intention.title)

            is OpenDocument ->
                ActivityResultContracts
                    .OpenDocument()
                    .createIntent(nativeApplication, intention.mimeTypes.toTypedArray())
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(intention.uri))
                        }
                        putExtra(Intent.EXTRA_MIME_TYPES, intention.mimeTypes.toTypedArray())
                    }

            is GetContent ->
                ActivityResultContracts
                    .GetContent()
                    .createIntent(nativeApplication, "*/*")
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(intention.uri))
                        }
                        putExtra(Intent.EXTRA_MIME_TYPES, intention.mimeTypes.toTypedArray())
                    }

            OpenBatteryOptimizationSettings ->
                Intent().apply {
                    @SuppressLint("BatteryLife")
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${nativeApplication.packageName}")
                }

            RequestMicrophonePermissionExternally ->
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:org.rhasspy.mobile.android")
                }

            is OpenLink ->
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(intention.link.url)
                }

            OpenAppSettings ->
                Intent().apply {
                    action = Settings.ACTION_SETTINGS
                }

            OpenOverlaySettings ->
                Intent().apply {
                    action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                }

            is ShareFile ->
                Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(intention.fileUri))
                        type = intention.mimeType
                    },
                    null
                )
        }
    }

}