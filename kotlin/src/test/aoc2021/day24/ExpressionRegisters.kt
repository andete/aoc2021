package test.aoc2021.day24

data class ExpressionRegister(val r: Register, var expression: Expression, var final: Long?) {
    override fun toString() = if (final != null) { "($expression:$final)" } else { "($expression:-)" }
}

data class ExpressionRegisters(val l: List<ExpressionRegister>) {
    operator fun get(r: Int) = l[r]
    operator fun get(r: Register) = l[r.r]
    override fun toString() = "w:${l[0]} x:${l[1]} y:${l[2]} z:${l[3]}"
}