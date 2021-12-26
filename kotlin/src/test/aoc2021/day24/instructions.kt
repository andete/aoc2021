package test.aoc2021.day24

import test.aoc2021.Argument

sealed class Instruction {
    abstract val a: Register
}
data class Inp(override val a: Register) : Instruction()
data class Add(override val a: Register, val b: Argument) : Instruction()
data class Mul(override val a: Register, val b: Argument) : Instruction()
data class Div(override val a: Register, val b: Argument) : Instruction()
data class Mod(override val a: Register, val b: Argument) : Instruction()
data class Eql(override val a: Register, val b: Argument) : Instruction()