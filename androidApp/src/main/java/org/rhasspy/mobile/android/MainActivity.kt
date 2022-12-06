package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.AppLaunchChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import org.rhasspy.mobile.AppActivity
import org.rhasspy.mobile.android.main.MainNavigation

@NoLiveLiterals
class MainActivity : AppActivity() {

    companion object {
        var isFirstLaunch = false
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(!AppLaunchChecker.hasStartedFromLauncher(this)){
            isFirstLaunch = true
        }
        AppLaunchChecker.onActivityCreate(this)

        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            MainNavigation()
        }
    }
}