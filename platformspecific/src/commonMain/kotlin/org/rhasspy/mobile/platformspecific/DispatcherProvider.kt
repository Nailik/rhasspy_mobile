package org.rhasspy.mobile.platformspecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

interface IDispatcherProvider {
    val IO: CoroutineDispatcher
    val Main: CoroutineDispatcher
}

internal class DispatcherProvider(
    override val IO: CoroutineDispatcher = Dispatchers.IO,
    override val Main: CoroutineDispatcher = Dispatchers.Main
) : IDispatcherProvider