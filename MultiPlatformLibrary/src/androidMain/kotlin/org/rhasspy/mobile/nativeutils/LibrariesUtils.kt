package org.rhasspy.mobile.nativeutils

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import org.rhasspy.mobile.Application

actual suspend fun loadLibraries(): List<Library> {
    val libs = Libs.Builder()
        .withContext(Application.Instance)
        .build()
    return libs.libraries
}