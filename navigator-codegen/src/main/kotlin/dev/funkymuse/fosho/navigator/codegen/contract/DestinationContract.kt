package dev.funkymuse.fosho.navigator.codegen.contract

import dev.funkymuse.fosho.navigator.codegen.argument.DeepLinkContract

interface DestinationContract {
    val deepLinksList: List<DeepLinkContract>
        get() = emptyList()
}