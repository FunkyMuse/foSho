package dev.funkymuse.fosho.sample.bottom_navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavBackStackEntry
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun BottomNavigation(
    modifier: Modifier = Modifier,
    navBackStackEntry: NavBackStackEntry?,
    showBottomNav: Boolean,
    bottomEntries: ImmutableList<BottomNavigationEntry>,
    onTopLevelClick: (route: String) -> Unit
) {
    AnimatedVisibility(
        visible = showBottomNav,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier.fillMaxWidth(),
    ) {
        BottomAppBar(modifier = Modifier.fillMaxWidth()) {
            bottomEntries.forEach { bottomEntry ->
                val isSelected =
                    navBackStackEntry?.destination?.route == bottomEntry.destination.destination
                NavigationBarItem(
                    selected = isSelected,
                    alwaysShowLabel = true,
                    onClick = {
                        onTopLevelClick(bottomEntry.route)
                    },
                    label = {
                        Text(
                            modifier = Modifier
                                .wrapContentSize(unbounded = true),
                            softWrap = false,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            text = stringResource(id = bottomEntry.text),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    icon = {
                        Crossfade(
                            targetState = isSelected,
                            label = "bottom-nav-icon"
                        ) { isSelectedIcon ->
                            if (isSelectedIcon) {
                                Icon(
                                    imageVector = bottomEntry.selectedIcon,
                                    contentDescription = stringResource(id = bottomEntry.text)
                                )
                            } else {
                                Icon(
                                    imageVector = bottomEntry.unselectedIcon,
                                    contentDescription = stringResource(id = bottomEntry.text)
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}
