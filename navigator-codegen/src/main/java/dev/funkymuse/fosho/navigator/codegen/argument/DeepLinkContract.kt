package dev.funkymuse.fosho.navigator.codegen.argument

data class DeepLinkContract(
    val uriPattern: String,
    val action: String,
    val mimeType: String? = null
)