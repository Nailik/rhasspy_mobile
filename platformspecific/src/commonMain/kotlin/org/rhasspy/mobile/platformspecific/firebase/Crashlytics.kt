package org.rhasspy.mobile.platformspecific.firebase

interface ICrashlytics {

    fun setEnabled(enabled: Boolean)

}

internal expect class Crashlytics() : ICrashlytics {

    override fun setEnabled(enabled: Boolean)

}