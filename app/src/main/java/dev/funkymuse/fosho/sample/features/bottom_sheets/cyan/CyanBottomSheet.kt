package dev.funkymuse.fosho.sample.features.bottom_sheets.cyan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.funkymuse.fosho.navigator.android.CyanBottomSheetDestination
import dev.funkymuse.fosho.navigator.android.CyanBottomSheetNavArguments
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object CyanBottomSheet : CyanBottomSheetDestination {
    @Composable
    override fun ColumnScope.Content() {
        val args = CyanBottomSheetNavArguments.rememberCyanBottomSheetNavArguments()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan.copy(alpha = 0.8f)),
        ) {
            Text(
                text = "Sheet dialog with argument id=${args.id}",
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}