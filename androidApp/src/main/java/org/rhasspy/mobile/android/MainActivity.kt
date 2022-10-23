package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.NoLiveLiterals
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import org.rhasspy.mobile.AppActivity
import org.rhasspy.mobile.android.ui.MainNavigation

@NoLiveLiterals
class MainActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            MainNavigation()
        }
    }
}