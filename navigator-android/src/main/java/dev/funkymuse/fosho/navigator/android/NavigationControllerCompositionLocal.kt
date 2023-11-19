package dev.funkymuse.fosho.navigator.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavHostController: ProvidableCompositionLocal<NavHostController?> =
    compositionLocalOf { null }

val navHostControllerOrThrow @Composable @ReadOnlyComposable get() = LocalNavHostController.current ?: throw IllegalArgumentException("LocalNavHostController should not be null")