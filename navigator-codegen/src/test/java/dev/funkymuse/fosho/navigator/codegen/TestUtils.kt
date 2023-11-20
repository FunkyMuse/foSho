package dev.funkymuse.fosho.navigator.codegen

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import dev.funkymuse.fosho.navigator.codegen.provider.NavigationProcessorProvider
import org.intellij.lang.annotations.Language
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals

internal fun compile(
    vararg source: SourceFile,
    temporaryFolder: TemporaryFolder,
    pluginParams: List<PluginOption> = emptyList(),
) =
    KotlinCompilation().apply {
        sources = source.toList()
        symbolProcessorProviders = listOf(NavigationProcessorProvider())
        workingDir = temporaryFolder.root
        inheritClassPath = true
        pluginOptions = pluginParams
        verbose = false
    }.compile()

internal fun assertSourceEquals(@Language("kotlin") expected: String, actual: String) {
    assertEquals(
        expected.trimIndent(),
        // unfortunate hack needed as we cannot enter expected text with tabs rather than spaces
        actual.trimIndent().replace("\t", "    ")
    )
}

internal fun KotlinCompilation.Result.sourceFor(fileName: String): String {
    return kspGeneratedSources().find { it.name == fileName }
        ?.readText()
        ?: throw IllegalArgumentException("Could not find file $fileName in ${kspGeneratedSources()}")
}

internal fun KotlinCompilation.Result.kspGeneratedSources(): List<File> {
    val kspWorkingDir = workingDir.resolve("ksp")
    val kspGeneratedDir = kspWorkingDir.resolve("sources")
    val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
    val javaGeneratedDir = kspGeneratedDir.resolve("java")
    return kotlinGeneratedDir.walk().toList() +
            javaGeneratedDir.walk().toList()
}

internal val KotlinCompilation.Result.workingDir: File
    get() = checkNotNull(outputDirectory.parentFile)