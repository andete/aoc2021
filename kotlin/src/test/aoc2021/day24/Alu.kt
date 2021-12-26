package test.aoc2021.day24

import test.aoc2021.Argument

data class Alu(val input: List<Long>, val instructions: List<Instruction>) {
    private var ic: Int = 0
    private var pc: Int = 0
    private val r = Registers(mutableListOf(0L, 0, 0, 0))

    fun inspect(register: Register) = r[register.r]

    fun run() {
        while (cycle()) {
        }
    }

    fun cycle(): Boolean {
        val i = instructions.getOrNull(pc) ?: return false
        pc++
        when (i) {
            is Inp -> {
                r[i.a.r] = input[ic]
                ic++
            }
            is Add -> {
                r[i.a.r] += read(i.b)
            }
            is Mul -> {
                r[i.a.r] *= read(i.b)
            }
            is Div -> {
                val b = read(i.b)
                if (b == 0L) {
                    throw IllegalStateException(i.toString())
                }
                r[i.a.r] /= b
            }
            is Mod -> {
                val a = r[i.a.r]
                val b = read(i.b)
                if (a < 0L || b <= 0L) {
                    throw IllegalStateException(i.toString())
                }
                r[i.a.r] %= b
            }
            is Eql -> {
                r[i.a.r] = if (r[i.a.r] == read(i.b)) {
                    1
                } else {
                    0
                }
            }
        }
        return true
    }

    private fun read(a: Argument): Long = when (a) {
        is Register -> r[a.r]
        is Direct -> a.d
        else -> throw IllegalStateException(a.toString())
    }
}

data class Registers(val l: MutableList<Long>) {
    operator fun get(r: Int) = l[r]
    override fun toString() = "w:${l[0]} x:${l[1]} y:${l[2]} z:${l[3]}"
    operator fun set(r: Int, value: Long) {
        l[r] = value
    }
}