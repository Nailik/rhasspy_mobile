package org.rhasspy.mobile.logic.nativeutils

import android.content.Intent
import android.content.Intent.EXTRA_MIME_TYPES
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import co.touchlab.kermit.Logger


abstract class AppActivity : AppCompatActivity() {
    val logger = Logger.withTag("AppActivity")

    private var resultCallback: ((activityResult: ActivityResult) -> Unit)? = null

    private val someActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            StartActivityForResult()
        ) {
            resultCallback?.invoke(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setup language
        setupLanguage()
    }

    fun createDocument(
        title: String,
        fileType: String,
        onResult: (activityResult: ActivityResult) -> Unit
    ) {
        resultCallback = onResult
        someActivityResultLauncher.launch(
            CreateDocument(fileType).createIntent(this, title)
        )
    }

    fun openDocument(types: Array<String>, onResult: (activityResult: ActivityResult) -> Unit) {
        resultCallback = onResult

        if (!tryOpenDocument(types)) {
            tryGetContent(types)
        }
    }

    private fun tryOpenDocument(types: Array<String>): Boolean {
        return try {
            val intent = OpenDocument().createIntent(this, types)
            if (SDK_INT >= Build.VERSION_CODES.O) {
                val uriDownload: Uri =
                    Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
                intent.putExtra(EXTRA_INITIAL_URI, uriDownload)
            }
            intent.putExtra(EXTRA_MIME_TYPES, types)
            someActivityResultLauncher.launch(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun tryGetContent(types: Array<String>): Boolean {
        return try {
            val intent = GetContent().createIntent(this, "*/*")
            if (SDK_INT >= Build.VERSION_CODES.O) {
                val uriDownload: Uri =
                    Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
                intent.putExtra(EXTRA_INITIAL_URI, uriDownload)
            }
            intent.putExtra(EXTRA_MIME_TYPES, types)
            someActivityResultLauncher.launch(intent)
            true
        } catch (exception: Exception) {
            logger.a(exception) { "tryGetContent failed" }
            false
        }
    }

}