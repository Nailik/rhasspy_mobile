package org.rhasspy.mobile.nativeutils

import com.mikepenz.aboutlibraries.entity.Library

expect suspend fun loadLibraries(): List<Library>