package org.rhasspy.mobile.platformspecific.features

expect object FeatureAvailability {

    val isPauseRecordingOnPlaybackFeatureEnabled: Boolean

    val isAudioEncodingOutputChangeEnabled: Boolean

}