package dev.funkymuse.fosho.navigator.codegen

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class NavigationProcessorDestinationTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `test generation single module destination`() {
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

@Graph(startingDestination = Home::class, rootGraph = true)
internal object HomeGraph

@Destination
@Argument(name = "hideBottomNav", argumentType = ArgumentType.BOOLEAN, defaultValue = DefaultBooleanValueFalse::class)
internal object Home : Screen
            """
        )

        val compilationResult = compile(kotlinSource, temporaryFolder = temporaryFolder)
        assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        assertSourceEquals(
            """package dev.funkymuse.fosho.navigator.android

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import dev.funkymuse.fosho.navigator.android.wrapped.currentEntry
import dev.funkymuse.fosho.navigator.android.wrapped.getResult
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentContract
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.sample.destinations.home.Home
import kotlin.Boolean
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

/**
 * Generated from [dev.funkymuse.fosho.sample.destinations.home.Home] 
 */
public interface HomeDestination : AndroidScreen {
  override val argumentsList: List<ArgumentContract>
    get() {
      return listOf(
      ArgumentContract(
      name = "hideBottomNav",
      argumentType=ArgumentType.BOOLEAN,
      isNullable = false) 
      )
    }

  override val arguments: List<NamedNavArgument>
    get() {
      return argumentsList.map{ toNavArgument(it) 
      }
    }

  override val deepLinks: List<NavDeepLink>
    get() {
      return Home.deepLinksList.map{ toDeepLink(it) 
      }
    }

  override val destination: String
    get() {
      return toDestinationRoute(route,argumentsList)
    }

  public companion object : HomeDestination {
    public const val route: String = "HomeDestination"

    public fun openHome(hideBottomNav: Boolean): String {
      val arguments = buildList<ArgumentContract> {
      add(ArgumentContract(
      name = "hideBottomNav",
      argumentType=ArgumentType.BOOLEAN,
      isNullable = false,
      defaultValue = hideBottomNav))
      }
      return toOpenFunction(arguments,route)
    }

    @Composable
    override fun AnimatedContentScope.Content(): Unit = TODO("STUB")
  }
}""",
            compilationResult.sourceFor("HomeDestination.kt")
        )
    }


}