package dev.funkymuse.fosho.navigator.codegen.contract

interface Dialog : DestinationContract {
    val dismissOnBackPress: Boolean get() = true
    val dismissOnClickOutside: Boolean get() = true
    val securePolicy: SecureFlagPolicyTemplate get() = SecureFlagPolicyTemplate.Inherit
    val usePlatformDefaultWidth: Boolean get() = true
    val decorFitsSystemWindows: Boolean get() = true

    enum class SecureFlagPolicyTemplate {
        Inherit,
        SecureOn,
        SecureOff
    }
}