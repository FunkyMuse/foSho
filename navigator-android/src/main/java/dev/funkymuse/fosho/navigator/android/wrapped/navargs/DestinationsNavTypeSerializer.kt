package dev.funkymuse.fosho.navigator.android.wrapped.navargs
/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
interface DestinationsNavTypeSerializer<T : Any> {
    fun toRouteString(value: T): String

    fun fromRouteString(routeStr: String): T
}
