package test.aoc2021.day24

import test.aoc2021.Argument

data class Register(val r: Int) : Argument {
    override fun toString() = when (r) {
        0 -> "w"
        1 -> "x"
        2 -> "y"
        3 -> "z"
        else -> throw IllegalStateException(r.toString())
    }
}

val w = Register(0)
val x = Register(1)
val y = Register(2)
val z = Register(3)