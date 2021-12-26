package test.aoc2021.day24

data class Inputs(val inputs: MutableMap<Int, Long> = mutableMapOf()) {
    operator fun get(i: Int) = inputs[i]
}

class LearningALu(val instructions: List<Instruction>, val inputs: Inputs = Inputs()) {
    val numStates = instructions.size + 1
    val r = listOf(
        ExpressionRegisters(
            mutableListOf(
                ExpressionRegister(w, Direct(0L), 0),
                ExpressionRegister(x, Direct(0L), 0),
                ExpressionRegister(y, Direct(0L), 0),
                ExpressionRegister(z, Direct(0L), 0)
            )
        )
    ) + (1 until numStates).map {
        ExpressionRegisters(
            mutableListOf(
                ExpressionRegister(w, Unknown(), null),
                ExpressionRegister(x, Unknown(), null),
                ExpressionRegister(y, Unknown(), null),
                ExpressionRegister(z, Unknown(), null)
            )
        )
    }

    fun learnForwards() {
        var ic = 0
        for (pc in instructions.indices) {
            val i = instructions[pc]
            val rBefore = r[pc]
            val rAfter = r[pc + 1]
            listOf(w, x, y, z).forEach { o ->
                if (rAfter[o].expression is Unknown) {
                    rAfter[o].expression = rBefore[o].expression
                }
            }
            when (i) {
                is Inp -> {
                    val d = inputs[ic]?.let {
                        Direct(it)
                    } ?: Input(ic)
                    rAfter[i.a].expression = d
                    ++ic
                }
                is Add -> {
                    val a = rBefore[i.a].expression
                    if (a is Unknown) {
                        continue
                    }
                    val b = i.b
                    if (a is Direct && b is Direct) {
                        rAfter[i.a].expression = Direct(a.d + b.d)
                    } else if (b is Direct) {
                        rAfter[i.a].expression = AddExp(a, b)
                    } else if (b is Register) {
                        rAfter[i.a].expression = AddExp(a, rBefore[b].expression)
                    }
                }
                is Mul -> {
                    val a = rBefore[i.a].expression
                    val b = i.b
                    if (b is Direct) {
                        if (b.d == 0L) {
                            rAfter[i.a].expression = Direct(0)
                            continue
                        }
                        if (b.d == 1L) {
                            rAfter[i.a].expression = rBefore[i.a].expression
                            continue
                        }
                    }
                    if (a is Unknown) {
                        continue
                    }
                    if (a is Direct && b is Direct) {
                        rAfter[i.a].expression = Direct(a.d * b.d)
                    } else if (b is Direct) {
                        rAfter[i.a].expression = MulExp(a, b)
                    } else if (b is Register) {
                        rAfter[i.a].expression = MulExp(a, rBefore[b].expression)
                    }
                }
                is Div -> {
                    val a = rBefore[i.a].expression
                    val b = i.b
                    if (b is Direct) {
                        if (b.d == 1L) {
                            rAfter[i.a].expression = rBefore[i.a].expression
                            continue
                        }
                    }
                    if (a is Unknown) {
                        continue
                    }
                    if (a is Direct && b is Direct) {
                        rAfter[i.a].expression = Direct(a.d / b.d)
                    } else if (b is Direct) {
                        rAfter[i.a].expression = DivExp(a, b)
                    } else if (b is Register) {
                        rAfter[i.a].expression = DivExp(a, rBefore[b].expression)
                    }
                }
                is Mod -> {
                    val a = rBefore[i.a].expression
                    val b = i.b
                    if (b is Direct) {
                        if (b.d == 1L) {
                            rAfter[i.a].expression = Direct(0)
                            continue
                        }
                        if (b.d < 0L) {
                            throw ArithmeticException()
                        }
                    }
                    if (a is Unknown) {
                        continue
                    }
                    if (a is Direct && b is Direct) {
                        rAfter[i.a].expression = Direct(a.d / b.d)
                    } else if (b is Direct) {
                        rAfter[i.a].expression = ModExp(a, b)
                    } else if (b is Register) {
                        rAfter[i.a].expression = ModExp(a, rBefore[b].expression)
                    }
                }
                is Eql -> {
                    val a = rBefore[i.a].expression
                    val b = i.b
                    if (a is Unknown) {
                        continue
                    }
                    if (a is Direct && b is Direct) {
                        val v = if (a.d == b.d) {
                            1L
                        } else {
                            0L
                        }
                        rAfter[i.a].expression = Direct(v)
                    } else if (b is Direct) {
                        rAfter[i.a].expression = EqlExp(a, b)
                    } else if (b is Register) {
                        rAfter[i.a].expression = EqlExp(a, rBefore[b].expression)
                    }
                }
            }
        }
    }

    fun learnBackwards(inputs: Inputs) {
        var ic = instructions.filterIsInstance<Inp>().count() - 1
        for (pc in instructions.indices.reversed()) {
            val i = instructions[pc]
            val rBefore = r[pc]
            val rAfter = r[pc + 1]
            when (i) {
                is Inp -> {
                    val afterExpression = rAfter[i.a].expression
                    // if we have a solution for after, we know the input
                    if (afterExpression is Direct) {
                        inputs.inputs[ic] = afterExpression.d
                    } else {
                        // otherwise, if we know the input, set it on after,
                        // else an InputExpression
                        rAfter[i.a].expression = inputs[ic]?.let {
                            Direct(it)
                        } ?: Input(ic)
                    }
                    ic--
                }
            }
        }
    }

    fun learnInputs(register: Register) {
        val e = r.last()[register]
        val v = e.final!!
        val o = e.expression as Operation
        o.inverse(inputs, Direct(v))
    }
}