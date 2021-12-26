package test.aoc2021.day24

import test.aoc2021.*

fun parseArgument(s: String): Argument {
    if (s == "w") {
        return w
    } else if (s == "x") {
        return x
    } else if (s == "y") {
        return y
    } else if (s == "z") {
        return z
    }
    return Direct(s.toLong())
}

fun parse(input: String): List<Instruction> {
    return input.lines().filter { it.isNotEmpty() }.map {
        val pn = it.split(' ')
        when (pn[0]) {
            "inp" -> Inp(parseArgument(pn[1]) as Register)
            "add" -> Add(parseArgument(pn[1]) as Register, parseArgument(pn[2]))
            "mul" -> Mul(parseArgument(pn[1]) as Register, parseArgument(pn[2]))
            "div" -> Div(parseArgument(pn[1]) as Register, parseArgument(pn[2]))
            "mod" -> Mod(parseArgument(pn[1]) as Register, parseArgument(pn[2]))
            "eql" -> Eql(parseArgument(pn[1]) as Register, parseArgument(pn[2]))
            else -> throw IllegalStateException(input)
        }
    }
}

fun longToInput(l: Long) = l.toString().map { it.toString().toLong() }
fun inputToLong(l: List<Long>) = l.map { it.toString() }.joinToString("").toLong()

fun check(monad: List<Instruction>, input: Long): Long {
    val l = longToInput(input)
    val a = Alu(l, monad)
    a.run()
    return a.inspect(z)
}