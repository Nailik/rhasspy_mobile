package org.rhasspy.mobile.android

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
import org.rhasspy.mobile.AppActivity
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.android.main.MainNavigation

/**
 * simple main activity to start application with splash screen
 */
@NoLiveLiterals
class MainActivity : AppActivity() {

    companion object {
        var isFirstLaunch = false
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!AppLaunchChecker.hasStartedFromLauncher(this)) {
            isFirstLaunch = true
        }
        AppLaunchChecker.onActivityCreate(this)

        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

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

        CoroutineScope(Dispatchers.IO).launch {
            Application.Instance.updateWidgetNative()
        }
    }
}