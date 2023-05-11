package org.rhasspy.mobile.widget

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform