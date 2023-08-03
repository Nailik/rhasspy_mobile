package org.rhasspy.mobile

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DummyIosTest {

    @Test
    fun iosTest() {
        assertTrue(true, "Check 'true' is true")
    }

    @Test
    fun iosTestFail() {
        assertFalse(false, "Check 'false' is false")
    }

}