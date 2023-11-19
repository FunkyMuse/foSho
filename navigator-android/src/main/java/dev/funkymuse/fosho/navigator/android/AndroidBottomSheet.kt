package dev.funkymuse.fosho.navigator.android

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentContract
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet

interface AndroidBottomSheet : NavigationDestination, BottomSheet {
    val argumentsList: List<ArgumentContract>
        get() = emptyList()

    @Composable
    fun ColumnScope.Content()
}
