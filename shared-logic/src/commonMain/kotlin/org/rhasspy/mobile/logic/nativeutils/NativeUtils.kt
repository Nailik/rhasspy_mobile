package org.rhasspy.mobile.logic.nativeutils

import org.koin.core.component.KoinComponent
import org.koin.core.component.get

expect fun isDebug(): Boolean

fun KoinComponent.openLink(url: String) = this.get<NativeApplication>().openLink(url)