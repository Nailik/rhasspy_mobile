package org.rhasspy.mobile.app

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DummyCommonTest {

    @Test
    fun commonTest() {
        assertTrue(true, "Check 'true' is true")
    }

    @Test
    fun commonTestFail() {
        assertFalse(false, "Check 'false' is false")
    }

}