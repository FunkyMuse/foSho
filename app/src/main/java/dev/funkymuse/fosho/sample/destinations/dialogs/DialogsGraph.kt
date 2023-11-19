package dev.funkymuse.fosho.sample.destinations.dialogs

import dev.funkymuse.fosho.navigator.codegen.annotation.Argument
import dev.funkymuse.fosho.navigator.codegen.annotation.CallbackArgument
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog

@Graph(
    startingDestination = YellowDialog::class,
    destinations = [RedDialog::class]
)
internal object DialogsGraph

@Destination
internal object YellowDialog : Dialog {
    override val usePlatformDefaultWidth: Boolean
        get() = false
}

@Destination(generateViewModelArguments = true)
@Argument(name = "texts", argumentType = ArgumentType.STRING_ARRAY, isNullable = true)
@CallbackArgument(name = "pickedText", argumentType = ArgumentType.STRING)
internal object RedDialog : Dialog {
    override val usePlatformDefaultWidth: Boolean
        get() = false
}
