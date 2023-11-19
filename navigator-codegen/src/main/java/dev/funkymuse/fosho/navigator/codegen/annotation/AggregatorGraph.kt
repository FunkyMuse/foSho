package dev.funkymuse.fosho.navigator.codegen.annotation

import dev.funkymuse.fosho.navigator.codegen.contract.DestinationContract
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class AggregatorGraph(
    val startingDestination: KClass<out DestinationContract>,
    val destinations: Array<KClass<out DestinationContract>> = [],
    val rootGraph : Boolean = false
)
