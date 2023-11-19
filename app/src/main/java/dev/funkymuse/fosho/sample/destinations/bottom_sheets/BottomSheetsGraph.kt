package dev.funkymuse.fosho.sample.destinations.bottom_sheets

import dev.funkymuse.fosho.navigator.codegen.annotation.Argument
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet

@Graph(startingDestination = GreenBottomSheet::class, destinations = [CyanBottomSheet::class])
internal object BottomSheetsGraph

@Destination
internal object GreenBottomSheet : BottomSheet

@Destination(generateScreenEntryArguments = true)
@Argument(name = "id", argumentType = ArgumentType.INT)
internal object CyanBottomSheet : BottomSheet

