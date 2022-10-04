package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import org.rhasspy.mobile.AppActivity
import org.rhasspy.mobile.android.navigation.RootNavigation
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission


class MainActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MicrophonePermission.init(this)
        OverlayPermission.init(this)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            RootNavigation()
        }
    }
}