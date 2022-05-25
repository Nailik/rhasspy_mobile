package org.rhasspy.mobile.services

import com.badoo.reaktive.observable.doOnAfterNext
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

/**
 * call the native indication and show/hide necessary indications
 */
object NativeIndicationService {

    init {
        IndicationService.currentState.doOnAfterNext {
            when (it) {
                IndicationState.Idle -> {
                    NativeIndication.closeIndicationOverOtherApps()
                    NativeIndication.releaseWakeUp()
                }
                IndicationState.Wakeup -> {
                    if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                        NativeIndication.wakeUpScreen()
                    }

                    if (AppSettings.isWakeWordLightIndication.data) {
                        NativeIndication.showIndication()
                    }
                }
                else -> {}
            }
        }
    }

}