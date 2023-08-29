package org.rhasspy.mobile.testutils

import org.kodein.mock.ArgConstraintsBuilder
import org.kodein.mock.Mocker
import org.kodein.mock.VerificationBuilder
import org.kodein.mock.tests.ITestsWithMocks

fun getRandomString(length: Int): String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

suspend fun ITestsWithMocks.coVerify(block: suspend VerificationBuilder.() -> Unit) {
    this.verifyWithSuspend(exhaustive = false, inOrder = false, block)
}

fun ITestsWithMocks.nVerify(block: VerificationBuilder.() -> Unit) {
    this.verify(exhaustive = false, inOrder = false, block)
}

suspend fun <T> ITestsWithMocks.coEvery(block: suspend ArgConstraintsBuilder.() -> T): Mocker.EverySuspend<T> {
    return this.everySuspending(block)
}