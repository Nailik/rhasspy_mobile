package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.NoLiveLiterals
import androidx.core.app.AppLaunchChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.widget.microphone.MicrophoneWidgetUtils


/**
 * simple main activity to start application with splash screen
 */
@NoLiveLiterals
class MainActivity : KoinComponent, AppCompatActivity() {

    companion object : KoinComponent {
        var isFirstLaunch = false
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!AppLaunchChecker.hasStartedFromLauncher(this)) {
            isFirstLaunch = true
        }
        AppLaunchChecker.onActivityCreate(this)

        onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            get<Navigator>().onBackPressed()
        }

        installSplashScreen().setKeepOnScreenCondition {
            //splash screen should be visible until the app has started
            !get<NativeApplication>().isHasStarted.value
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        if (intent.getBooleanExtra(IntentAction.StartRecording.param, false)) {
            if (MicrophonePermission.granted.value) {
                get<ServiceMiddleware>().action(WakeWordDetected(Source.Local, wakeWord = "intent"))
            }
        }

        val viewModelFactory = get<ViewModelFactory>()

        this.setContent {
            MainScreen(viewModelFactory)
        }

        CoroutineScope(Dispatchers.IO).launch {
            MicrophoneWidgetUtils.updateWidget()
        }
    }
}