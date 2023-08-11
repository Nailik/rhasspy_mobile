package org.rhasspy.mobile.app

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.ui.assistant.AssistantScreen
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.assist.AssistantUiEvent.Activate
import org.rhasspy.mobile.viewmodel.assist.AssistantViewModel

class AssistantActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: AssistantViewModel by inject()
    private val viewModelFactory: ViewModelFactory by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        handleIntent(intent)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        } // else handled by manifest attribute
        val isLocked = getSystemService<KeyguardManager>()?.isKeyguardLocked ?: false
        if (isLocked) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
        }

        setContent {
            AssistantScreen(viewModelFactory)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {

        if ((intent?.flags != null && intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) ||
            intent?.action in listOf(Intent.ACTION_ASSIST, "android.intent.action.VOICE_ASSIST")
        ) {
            viewModel.onEvent(Activate)

            viewModel.awaitIdle {
                println("AssistantActivity finish")
                finish()
            }
        }

    }

}