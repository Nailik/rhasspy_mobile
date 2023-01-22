package org.rhasspy.mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform