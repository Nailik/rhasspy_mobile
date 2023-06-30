package org.rhasspy.mobile.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.kodein.mock.tests.TestsWithMocks

actual abstract class IAppTestNew : TestsWithMocks() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

}
