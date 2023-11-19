package dev.funkymuse.fosho.navigator.codegen.annotation

import dev.funkymuse.fosho.navigator.codegen.contract.DestinationContract
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class AggregatorContent(val forContent: KClass<out DestinationContract>)
