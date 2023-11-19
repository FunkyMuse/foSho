package dev.funkymuse.fosho.sample.features.bottom_sheets.green

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.funkymuse.fosho.navigator.android.CyanBottomSheetDestination
import dev.funkymuse.fosho.navigator.android.GreenBottomSheetDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content
import kotlin.random.Random

@Content
internal object GreenBottomSheet : GreenBottomSheetDestination {
    @Composable
    override fun ColumnScope.Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green.copy(alpha = 0.9f)),
        ) {
            Button(onClick = {
                Navigator.navigateSingleTop(
                    CyanBottomSheetDestination.openCyanBottomSheet(
                        Random.nextInt(
                            1,
                            Int.MAX_VALUE
                        )
                    )
                )
            }) {
                Text(text = "Open other bottom sheet dialog", modifier = Modifier.padding(24.dp))
            }
        }
    }
}