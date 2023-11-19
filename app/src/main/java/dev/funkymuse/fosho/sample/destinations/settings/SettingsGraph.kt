package dev.funkymuse.fosho.sample.destinations.settings

import dev.funkymuse.fosho.navigator.android.DefaultBooleanValueFalse
import dev.funkymuse.fosho.navigator.android.wrapped.HIDE_BOTTOM_NAV_ARG
import dev.funkymuse.fosho.navigator.codegen.annotation.Argument
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.contract.Screen

@Graph(startingDestination = Settings::class)
internal object SettingsGraph

@Destination
@Argument(name = HIDE_BOTTOM_NAV_ARG, argumentType = ArgumentType.BOOLEAN, defaultValue = DefaultBooleanValueFalse::class)
internal object Settings : Screen