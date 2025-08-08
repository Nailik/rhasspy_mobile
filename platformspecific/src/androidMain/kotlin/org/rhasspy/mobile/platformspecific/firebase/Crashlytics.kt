package org.rhasspy.mobile.platformspecific.firebase

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics


internal actual class Crashlytics : ICrashlytics {

    actual override fun setEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

}