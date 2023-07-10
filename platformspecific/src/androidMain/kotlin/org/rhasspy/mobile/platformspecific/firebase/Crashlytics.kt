package org.rhasspy.mobile.platformspecific.firebase

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

internal actual class Crashlytics : ICrashlytics {

    actual override fun setEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

}