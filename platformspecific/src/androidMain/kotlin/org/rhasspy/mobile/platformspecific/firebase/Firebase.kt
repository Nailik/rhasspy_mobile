package org.rhasspy.mobile.platformspecific.firebase

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

actual fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
}