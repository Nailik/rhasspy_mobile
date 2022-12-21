package org.rhasspy.mobile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


abstract class AppActivity : ComponentActivity() {

    private var resultCallback: ((activityResult: ActivityResult) -> Unit)? = null

    private val someActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            resultCallback?.invoke(it)
        }

    fun createDocument(
        title: String,
        fileType: String,
        onResult: (activityResult: ActivityResult) -> Unit
    ) {
        resultCallback = onResult
        someActivityResultLauncher.launch(
            ActivityResultContracts.CreateDocument(fileType).createIntent(this, title)
        )
    }

    fun openDocument(types: Array<String>, onResult: (activityResult: ActivityResult) -> Unit) {
        resultCallback = onResult
        val intent = ActivityResultContracts.OpenDocument().createIntent(this, types)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.putExtra(
                EXTRA_INITIAL_URI,
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val uriDownload: Uri =
                Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
            intent.putExtra(EXTRA_INITIAL_URI, uriDownload)
        }
        someActivityResultLauncher.launch(intent)
    }
}