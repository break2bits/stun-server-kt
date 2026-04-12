package com.break2bits.cli

abstract class ArgDefinition(
    protected val shortKey: String,
    protected val longKey: String,
) {

    fun matches(value: String?): Boolean {
        if (value == null) {
            return false;
        }
        return shortKey == value || longKey == value;
    }
}