package dev.funkymuse.fosho.navigator.codegen

import com.squareup.kotlinpoet.ClassName
import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH

internal object ClassNames {

    object KotlinXImmutable {
        const val IMMUTABLE_PACKAGE = "kotlinx.collections.immutable"
        const val ImmutableList = "ImmutableList"
        const val toImmutableList = "toImmutableList"
    }

    object Android {
        val Uri = ClassName("android.net", "Uri")
        const val ANDROIDX_NAVIGATION = "androidx.navigation"

        val NAMED_NAV_ARGUMENT = ClassName(
            packageName = ANDROIDX_NAVIGATION,
            Constants.NamedNavArgument
        )
        val NAV_DEEP_LINK = ClassName(
            packageName = ANDROIDX_NAVIGATION,
            Constants.NavDeepLink
        )
        val NAV_BACK_STACK_ENTRY = ClassName(ANDROIDX_NAVIGATION, Constants.NavBackStackEntry)
        val ANDROIDX_SAVED_STATE_HANDLE = ClassName("androidx.lifecycle", "SavedStateHandle")
        val ANDROIDX_NAV_HOST_CONTROLLER = ClassName(ANDROIDX_NAVIGATION, Constants.NavHostController)
    }

    object Compose {
        private const val ANDROIDX_COMPOSE = "androidx.compose"
        const val COMPOSE_REMEMBER = "$ANDROIDX_COMPOSE.runtime.remember"
        const val ANDROIDX_COMPOSE_ANIMATION = "$ANDROIDX_COMPOSE.animation"
        const val ANDROIDX_COMPOSE_FOUNDATION_LAYOUT = "$ANDROIDX_COMPOSE.foundation.layout"
        const val dialogProperties = "dialogProperties"

        val composeDialogProperties = ClassName("$ANDROIDX_COMPOSE.ui.window", "DialogProperties")
        val composable = ClassName("$ANDROIDX_COMPOSE.runtime", "Composable")
        val animatedContentScope = ClassName(ANDROIDX_COMPOSE_ANIMATION, Constants.AnimatedContentScope)
        val columnScope = ClassName(ANDROIDX_COMPOSE_FOUNDATION_LAYOUT, Constants.ColumnScope)
    }

    object Codegen {
        const val ARGUMENTABLE_PACKAGE =
            "dev.funkymuse.fosho.navigator.codegen.argument"
        val ArgumentContract = ClassName(ARGUMENTABLE_PACKAGE, Constants.ArgumentContract)
        val ArgumentType = ClassName(ARGUMENTABLE_PACKAGE, Constants.ArgumentType)

        fun localClassNameFor(
            name: String
        ): ClassName = ClassName(
            packageName = LOCAL_PATH,
            name
        )
    }

    object CodegenAndroid {
        const val LOCAL_PATH = "dev.funkymuse.fosho.navigator.android"
        val CallbackArguments = ClassName(LOCAL_PATH, Constants.CallbackArguments)
    }

    object WrappedNavigation {
        const val COMPOSED_NAVIGATION_PACKAGE = "dev.funkymuse.fosho.navigator.android.wrapped"
        const val COMPOSED_VIEW_MODEL = "$COMPOSED_NAVIGATION_PACKAGE.arguments.viewmodel"
        const val COMPOSED_NAV_ENTRY = "$COMPOSED_NAVIGATION_PACKAGE.arguments.nav_entry"
        const val currentEntry = "currentEntry"
        const val getResult = "getResult"
        val ViewModelNavigationArguments =
            ClassName(COMPOSED_VIEW_MODEL, "ViewModelNavigationArguments")
        val NavigationEntryArguments = ClassName(COMPOSED_NAV_ENTRY, "NavigationEntryArguments")
    }
}