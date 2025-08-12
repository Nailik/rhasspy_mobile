package org.rhasspy.mobile.logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.rhasspy.mobile.platformspecific.IDispatcherProvider

internal class TestDispatcherProvider @OptIn(ExperimentalCoroutinesApi::class) constructor(
    override val IO: CoroutineDispatcher = UnconfinedTestDispatcher(),
    override val Main: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : IDispatcherProvider