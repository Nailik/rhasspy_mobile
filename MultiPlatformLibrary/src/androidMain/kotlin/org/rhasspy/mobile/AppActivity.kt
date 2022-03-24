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

    fun createDocument(title: String, onResult: (activityResult: ActivityResult) -> Unit) {
        resultCallback = onResult
        someActivityResultLauncher.launch(ActivityResultContracts.CreateDocument().createIntent(this, title))
    }
}