package dev.funkymuse.fosho.navigator.android

interface AndroidGraph {
    val route: String

    val dialogs: List<AndroidDialog>
        get() = emptyList()

    val screens: List<AndroidScreen>
        get() = emptyList()

    val bottomSheets: List<AndroidBottomSheet>
        get() = emptyList()

    val startingDestination: NavigationDestination
}
