package org.rhasspy.mobile.app

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AppLaunchChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.intent.IntentActionType
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.widget.microphone.MicrophoneWidgetUtils


/**
 * simple main activity to start application with splash screen
 */
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
            get<INavigator>().onBackPressed()
        }

        installSplashScreen().setKeepOnScreenCondition {
            //splash screen should be visible until the app has started
            !get<NativeApplication>().isHasStarted.value
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val viewModelFactory = get<ViewModelFactory>()

        if (intent.getBooleanExtra(IntentActionType.StartRecording.param, false)) {
            viewModelFactory.getViewModel<HomeScreenViewModel>().onEvent(MicrophoneFabClick)
        }

        this.setContent {
            MainScreen(viewModelFactory)
        }

        CoroutineScope(Dispatchers.IO).launch {
            MicrophoneWidgetUtils.updateWidget()
        }
    }
}