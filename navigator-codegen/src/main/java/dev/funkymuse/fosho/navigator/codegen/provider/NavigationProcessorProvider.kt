package dev.funkymuse.fosho.navigator.codegen.provider

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.processor.NavigationProcessor

class NavigationProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        NavigationProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
            isSingleModule = !(environment.options["${Constants.foSho}.${Constants.multiModule}"]?.toBooleanStrict()
                ?: true)
        )
}