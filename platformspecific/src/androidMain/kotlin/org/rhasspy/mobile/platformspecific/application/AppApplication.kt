package org.rhasspy.mobile.platformspecific.application

import androidx.multidex.MultiDexApplication

@OptIn(ExperimentalMultiplatform::class)
//@AllowDifferentMembersInActual
actual open class AppApplication : MultiDexApplication()