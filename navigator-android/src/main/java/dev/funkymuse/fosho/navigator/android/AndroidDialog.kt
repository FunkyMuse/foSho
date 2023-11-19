package dev.funkymuse.fosho.navigator.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentContract

interface AndroidDialog : NavigationDestination, Dialog {
    val dialogProperties: DialogProperties

    val argumentsList: List<ArgumentContract>
        get() = emptyList()

    @Composable
    fun Content()
}