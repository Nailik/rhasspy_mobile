package org.rhasspy.mobile

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

abstract class AppActivity : ComponentActivity() {

    private var resultCallback: ((activityResult: ActivityResult) -> Unit)? = null

    private val someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        resultCallback?.invoke(it)
    }

    fun createDocument(title: String, fileType: String, onResult: (activityResult: ActivityResult) -> Unit) {
        resultCallback = onResult
        someActivityResultLauncher.launch(ActivityResultContracts.CreateDocument(fileType).createIntent(this, title))
    }

    fun openDocument(types: Array<String>, onResult: (activityResult: ActivityResult) -> Unit) {
        resultCallback = onResult
        someActivityResultLauncher.launch(ActivityResultContracts.OpenDocument().createIntent(this, types))
    }
}