package org.rhasspy.mobile.shared_viewmodel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform