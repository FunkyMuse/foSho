package dev.funkymuse.fosho.navigator.codegen.codegen


import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.ClassNames.KotlinXImmutable.IMMUTABLE_PACKAGE
import dev.funkymuse.fosho.navigator.codegen.ClassNames.KotlinXImmutable.ImmutableList
import dev.funkymuse.fosho.navigator.codegen.ClassNames.KotlinXImmutable.toImmutableList
import dev.funkymuse.fosho.navigator.codegen.Constants
import java.io.OutputStream

internal fun graphFactoryGeneratorStream(
    codeGenerator: CodeGenerator
): OutputStream = codeGenerator.createNewFile(
    dependencies = Dependencies(true),
    packageName = LOCAL_PATH,
    fileName = Constants.GraphFactory
).also {
    it += "${Constants.`package`} $LOCAL_PATH\n\n"
    it += "${Constants.import} $IMMUTABLE_PACKAGE.$ImmutableList\n" +
            "${Constants.import} $IMMUTABLE_PACKAGE.$toImmutableList\n" +
            "${Constants.import} $LOCAL_PATH.${Constants.AndroidGraph}\n"
    it += "\n\n${Constants.`object`} ${Constants.GraphFactory}"
}

internal fun addGraphsVariable() =
    "\n\n\tval graphs: $ImmutableList<${Constants.AndroidGraph}> = ${Constants.listOf}(\n"

internal operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
}