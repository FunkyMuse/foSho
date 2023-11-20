package dev.funkymuse.fosho.navigator.codegen

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class NavigationProcessorContentTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `test generation single module graph`() {
        val kotlinSource = SourceFile.kotlin(
            "HomeGraph.kt", """
package dev.funkymuse.fosho.sample.destinations.home

import dev.funkymuse.fosho.navigator.android.DefaultBooleanValueFalse
import dev.funkymuse.fosho.navigator.android.wrapped.HIDE_BOTTOM_NAV_ARG
import dev.funkymuse.fosho.navigator.codegen.annotation.Argument
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.contract.Screen
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import dev.funkymuse.fosho.navigator.android.HomeDestination
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Graph(startingDestination = Home::class, rootGraph = true)
internal object HomeGraph

@Destination
@Argument(name = "hideBottomNav", argumentType = ArgumentType.BOOLEAN, defaultValue = DefaultBooleanValueFalse::class)
internal object Home : Screen

@Content
internal object HomeContent : HomeDestination {
    @Composable
    override fun AnimatedContentScope.Content() {
        
    }
}
            """
        )

        val compilationResult = compile(kotlinSource, temporaryFolder = temporaryFolder)
        assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        println(compilationResult.sourceFor("HomeNavigationGraph.kt"))
        assertSourceEquals(
            """package dev.funkymuse.fosho.navigator.android

import kotlin.String
import kotlin.collections.List

/**
 * Generated from [dev.funkymuse.fosho.sample.destinations.home.HomeGraph] 
 */
public object HomeNavigationGraph : AndroidGraph {
  override val route: String
    get() {
      return "HomeNavigationGraph"
    }

  override val screens: List<AndroidScreen> =
      listOf(dev.funkymuse.fosho.sample.destinations.home.HomeContent)

  override val dialogs: List<AndroidDialog> = listOf()

  override val bottomSheets: List<AndroidBottomSheet> = listOf()

  override val startingDestination: NavigationDestination =
      dev.funkymuse.fosho.sample.destinations.home.HomeContent
}
""",
            compilationResult.sourceFor("HomeNavigationGraph.kt")
        )
    }


}