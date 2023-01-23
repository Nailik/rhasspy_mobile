package org.rhasspy.mobile.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.AppLaunchChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.nativeutils.AppActivity
import org.rhasspy.mobile.android.main.MainNavigation
import org.rhasspy.mobile.logic.nativeutils.NativeApplication
import org.rhasspy.mobile.viewmodel.screens.HomeScreenViewModel


/**
 * simple main activity to start application with splash screen
 */
@NoLiveLiterals
class MainActivity : KoinComponent, AppActivity() {

    companion object : KoinComponent {
        var isFirstLaunch = false
            private set

        fun startRecordingAction() {
            val application = get<NativeApplication>()
            application.currentActivity?.also {
                it.startActivity(
                    Intent(application, MainActivity::class.java).apply {
                        putExtra(IntentAction.StartRecording.param, true)
                    }
                )
            } ?: run {
                application.startActivity(
                    Intent(application, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(IntentAction.StartRecording.param, true)
                    }
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!AppLaunchChecker.hasStartedFromLauncher(this)) {
            isFirstLaunch = true
        }
        AppLaunchChecker.onActivityCreate(this)

        installSplashScreen().setKeepOnScreenCondition {
            //splash screen should be visible until the app has started
            !get<NativeApplication>().isHasStarted.value
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val value = intent.getBooleanExtra(IntentAction.StartRecording.param, false)
        get<HomeScreenViewModel>().startRecordingAction(value)

        this.setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavigation()
                if (BuildConfig.DEBUG) {
                    Text(
                        text = "DEBUG",
                        modifier = Modifier
                            .rotate(45F)
                            .offset(50.dp)
                            .background(Color.Red)
                            .width(150.dp)
                            .align(Alignment.TopEnd),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            get<NativeApplication>().updateWidgetNative()
        }
    }
}