package dev.funkymuse.fosho.navigator.android

import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentValue

object DefaultBooleanValueFalse : ArgumentValue {
    override val defaultValue: Boolean = false
}

object DefaultBooleanValueTrue : ArgumentValue {
    override val defaultValue: Boolean = true
}

object DefaultIntValueZero : ArgumentValue {
    override val defaultValue: Int = 0
}

object DefaultIntValueMinusOne : ArgumentValue {
    override val defaultValue: Int = -1
}

object DefaultValueNull : ArgumentValue {
    override val defaultValue get() = null
}