package dev.funkymuse.fosho.sample.destinations.login

import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.contract.Screen

@Graph(startingDestination = Login::class)
internal object LoginGraph

@Destination
internal object Login : Screen