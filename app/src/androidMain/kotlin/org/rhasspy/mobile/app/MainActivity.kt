package org.rhasspy.mobile.app

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.core.app.AppLaunchChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.IMainActivity
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
class MainActivity : IMainActivity(), KoinComponent {

    private val viewModelFactory: ViewModelFactory by inject()
    private val nativeApplication: NativeApplication by inject()
    private val navigator: INavigator by inject()

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
            navigator.onBackPressed()
        }

        installSplashScreen().setKeepOnScreenCondition {
            //splash screen should be visible until the app has started
            !nativeApplication.isHasStarted.value
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

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