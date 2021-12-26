package test.aoc2021.day24

import test.aoc2021.Argument

interface Expression {
    fun evaluate(inputs: Inputs): Expression
    val noRec: String
}

data class Direct(val d: Long) : Argument, Expression {
    override fun evaluate(inputs: Inputs) = this
    override val noRec = d.toString()
}

interface Operation {
    // this will give only one solution even if there may be multiple
    fun inverse(inputs: Inputs, v: Expression)
}

data class AddExp(val a: Expression, val b: Expression) : Expression, Operation {

    override val noRec = "+"
    override fun toString() = "[+:${a.noRec}:${b.noRec}]"

    override fun evaluate(inputs: Inputs): Expression {
        if (a is Direct && b is Direct) {
            return Direct(a.d + b.d)
        }
        return AddExp(a.evaluate(inputs), b.evaluate(inputs))
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        check(a is Operation)
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        if (v is Direct && b is Direct) {
            return a.inverse(inputs, Direct(v.d - b.d))
        }
        return a.inverse(inputs, SubExp(v, b))
    }
}

data class SubExp(val a: Expression, val b: Expression) : Expression, Operation {
    override val noRec = "-"
    override fun toString() = "[-:${a.noRec}:${b.noRec}]"

    override fun evaluate(inputs: Inputs): Expression {
        if (a is Direct && b is Direct) {
            return Direct(a.d - b.d)
        }
        return SubExp(a.evaluate(inputs), b.evaluate(inputs))
    }
    override fun inverse(inputs: Inputs, v: Expression) {
        check(a is Operation)
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        if (v is Direct && b is Direct) {
            return a.inverse(inputs, Direct(v.d + b.d))
        }
        return a.inverse(inputs, AddExp(v, b))
    }
}

data class MulExp(val a: Expression, val b: Expression) : Expression, Operation {
    override val noRec = "*"
    override fun toString() = "[*:${a.noRec}:${b.noRec}]"

    override fun evaluate(inputs: Inputs): Expression {
        if (a is Direct && b is Direct) {
            return Direct(a.d * b.d)
        }
        return MulExp(a.evaluate(inputs), b.evaluate(inputs))
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        if (a is Direct) {
            return
        }
        if (a !is Operation) {
            throw IllegalStateException()
        }
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        if (v is Direct && b is Direct) {
            return a.inverse(inputs, Direct(v.d / b.d))
        }
        return a.inverse(inputs, DivExp(v, b))
    }
}

data class DivExp(val a: Expression, val b: Expression) : Expression, Operation {
    override val noRec = "/"
    override fun toString() = "[/:${a.noRec}:${b.noRec}]"

    override fun evaluate(inputs: Inputs): Expression {
        if (a is Direct && b is Direct) {
            return Direct(a.d / b.d)
        }
        return DivExp(a.evaluate(inputs), b.evaluate(inputs))
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        check(a is Operation)
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        if (v is Direct && b is Direct) {
            return a.inverse(inputs, Direct(v.d * b.d))
        }
        return a.inverse(inputs, MulExp(v, b))
    }
}

data class ModExp(val a: Expression, val b: Expression) : Expression, Operation {
    override val noRec = "%"
    override fun toString() = "[%:${a.noRec}:${b.noRec}]"

    override fun evaluate(inputs: Inputs): Expression {
        if (a is Direct && b is Direct) {
            return Direct(a.d + b.d)
        }
        return ModExp(a.evaluate(inputs), b.evaluate(inputs))
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        check(a is Operation)
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        if (v is Direct && b is Direct) {
            return a.inverse(inputs, Direct(v.d % b.d))
        }
        return a.inverse(inputs, ModExp(v, b))
    }
}

data class EqlExp(val a: Expression, val b: Expression) : Expression, Operation {
    override val noRec = "="
    override fun toString() = "[=:${a.noRec}:${b.noRec}]"
    override fun evaluate(inputs: Inputs): Expression {
        val a1 = a.evaluate(inputs)
        val b1 = b.evaluate(inputs)
        if (a1 is Direct && b1 is Direct) {
            return Direct(
                if (a1 == b1) {
                    1L
                } else {
                    0
                }
            )
        }
        return EqlExp(a1, b1)
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        val b = b.evaluate(inputs)
        val v = v.evaluate(inputs)
        check(a is Operation)
        TODO()
//        if (v == 1L) {
//            return b
//        } else if (v == 0L) {
//            return b + 1
//        } else {
//            throw IllegalStateException()
//        }
    }
}

data class Input(val ic: Int) : Expression, Operation {
    override val noRec = "I$ic"
    override fun toString() = "I$ic"
    override fun evaluate(inputs: Inputs): Expression {
        if (ic in inputs.inputs.keys) {
            return Direct(inputs[ic]!!)
        }
        return this
    }

    override fun inverse(inputs: Inputs, v: Expression) {
        if (v is Direct) {
            inputs.inputs[ic] = v.d
            return
        }
        throw IllegalStateException(inputs.toString() + v.toString())
    }
}

class Unknown : Expression {
    override val noRec = "U"
    override fun toString() = "U"
    override fun evaluate(inputs: Inputs): Expression {
        throw IllegalStateException(toString())
    }
}
