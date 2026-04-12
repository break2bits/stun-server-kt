package com.break2bits.cli

class StunServerArgParser(private val args: Array<String>) {
    fun getInt(argDef: IntArgDefinition): Int {
        var parseNext = false;
        args.forEach {
            if (parseNext) {
                return it.toInt();
            }
            if (argDef.matches(it)) {
                parseNext = true;
            }
        }

        val default = argDef.getDefault() ?: throw IllegalArgumentException("Missing required argument $argDef")
        return default;
    }

    fun getBool(argDef: BoolArgDefinition): Boolean {
        args.forEach {
            if (argDef.matches(it)) {
                return true;
            }
        }

        val default = argDef.getDefault() ?: throw IllegalArgumentException("Missing required argument $argDef")
        return default;
    }
}