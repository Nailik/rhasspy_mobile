package org.rhasspy.mobile.koin

import io.ktor.utils.io.core.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.definition.Definition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.onClose

/**
 * getSafe returns null when the class cannot be found in the module definition
 * this resolves the issue, that a class cannot be loaded when koin modules
 * are reloaded for or after testing
 *
 * use this when the get can be executed while test is loading
 * - overlay
 * - widget
 */
inline fun <reified T : Any> KoinComponent.getSafe(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T? {
    return try {
        get(qualifier, parameters)
    } catch (e: NoBeanDefFoundException) {
        null
    }
}

inline fun <reified T : Closeable> Module.closeableSingle(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>
) {
    single(qualifier, createdAtStart, definition) onClose {
        it?.close()
    }
}