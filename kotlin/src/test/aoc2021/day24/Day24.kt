package test.aoc2021

import org.junit.jupiter.api.Test
import test.aoc2021.day24.*
import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.test.assertEquals

interface Argument

class Day24 {

    private val exampleBits = """inp w
add z w
mod z 2
div w 2
add y w
mod y 2
div w 2
add x w
mod x 2
div w 2
mod w 2"""

    @Test
    fun example1() {
        val a = Alu(listOf(5), listOf(Inp(x), Mul(x, Direct(-1))))
        a.cycle()
        a.cycle()
        assertEquals(-5, a.inspect(x))
    }

    @Test
    fun example2() {
        val program = listOf(
            Inp(z),
            Inp(x),
            Mul(z, Direct(3)),
            Eql(z, x)
        )
        val a = Alu(
            listOf(5, 3),
            program
        )
        a.run()
        assertEquals(0, a.inspect(z))
        val b = Alu(listOf(5, 15), program)
        b.run()
        assertEquals(1, b.inspect(z))

        val p2 = parse(
            """inp z
inp x
mul z 3
eql z x"""
        )
        println(p2)
    }

    @Test
    fun monadTesting1() {
        val monad = parse(File("../input/input24.txt").readText())
        println(monad)
        val l = longToInput(13579246899999)
        println(l)
        val a = Alu(l, monad)
        a.run()
        println(a.inspect(z))
        println(check(monad, 13579246899999))
        println(check(monad, 11111111111111).toString(16))
        println(check(monad, 11111111111111).toString(16))
    }

    @Test
    fun example1learning() {
        val a = LearningALu(listOf(Inp(x), Mul(x, Direct(-1))))
        // when we have result -5
        a.r.last()[x].expression = Unknown()
        a.r.last()[x].final = -5
        a.learnForwards()
        a.learnInputs(x)
        assertEquals(5, a.inputs.inputs[0])
    }

    @Test
    fun example2learning() {
        val a = LearningALu(listOf(Inp(x), Add(x, Direct(5))))
        // when we have result 11
        a.r.last()[x].expression = Unknown()
        a.r.last()[x].final = 11
        a.learnForwards()
        a.learnInputs(x)
        assertEquals(6, a.inputs.inputs[0])
    }

    @Test
    fun example3learning() {
        val a = LearningALu(parse(exampleBits))
        // when we have result [1,1,0,0]
        // expect to learn input 12 in w
        a.r.last()[w].final = 1
        a.r.last()[x].final = 1
        a.r.last()[y].final = 0
        a.r.last()[z].final = 0
        a.learnForwards()
        a.learnInputs(w)
        println(a.inputs)
        assertEquals(8, a.inputs[0])
    }

    @Test
    fun monadTestingLearning1() {
        val monad = parse(File("../input/input24.txt").readText())
        val a = LearningALu(monad)
        a.r.last()[z].final = 0
        a.learnForwards()
        a.learnInputs(z)
        println(a.inputs)
    }

    @Test
    fun test1() {
        val monad = parse(File("../input/input24.txt").readText())
        val input = 96276563227997.toString().map { it.toString().toLong() }
        val a = Alu(input, monad)
        a.run()
        assertEquals(0, a.inspect(z))
    }

    @Test
    fun test2() {
        val monad = parse(File("../input/input24.txt").readText())
        val input = 96299896449997.toString().map { it.toString().toLong() }
        val a = Alu(input, monad)
        a.run()
        assertEquals(0, a.inspect(z))
    }

    @Test
    fun test3() {
        val monad = parse(File("../input/input24.txt").readText())
        val input = 16262141149991.toString().map { it.toString().toLong() }
        val a = Alu(input, monad)
        a.run()
        assertEquals(0, a.inspect(z))
    }

    @Test
    fun part1() {
        val monad = parse(File("../input/input24.txt").readText())
        // initial value was found using previous programs which got reworked/removed
        var il = 96276563227997
        val a = longToInput(il).toMutableList()

        (0..13).forEach { loop ->
            println("loop $loop")
            for (i in (0..13)) {
                for (j in (0..13)) {
                    val ax = a.toMutableList()
                    for (vi in (9L downTo 1)) {
                        for (vj in (9L downTo 1)) {
                            ax[i] = vi
                            ax[j] = vj
                            val alu = Alu(ax, monad)
                            alu.run()
                            val il2 = inputToLong(ax)
                            if (alu.inspect(z) == 0L && inputToLong(ax) > il) {
                                il = il2
                                println(il2)
                                a[i] = vi
                                a[j] = vj
                            }
                        }
                    }
                }
            }
        }
        println(il)
        assertEquals(96299896449997, il)

    }

    @Test
    fun part2() {
        val r = Random(System.currentTimeMillis())
        val monad = parse(File("../input/input24.txt").readText())
        // var il = 16262141149991 + 1
        var found = Long.MAX_VALUE
        for (outerLoop in 1..1000) {
            // println("ol $outerLoop")
            val a = (longToInput(16262141149991).map { (r.nextInt(9) + 1).toLong() }).toMutableList()
            var prevZ: Long? = null
            for (loop in 1..1000) {
                var minM = Long.MAX_VALUE
                var minI = 0
                var minJ = 1L
                for (i in 2..13) {
                    val res = (1L..9).map {
                        val a2 = a.toMutableList()
                        a2[i] = it
                        val alu = Alu(a2, monad)
                        alu.run()
                        val v = alu.inspect(z).absoluteValue
                        it to v
                    }
                    val x = res.minByOrNull { it.second }!!
                    if (x.second < minM) {
                        minM = x.second
                        minJ = x.first
                        minI = i
                    }
                }
                a[minI] = minJ
                val z = inputToLong(a)
                if (minM == 0L) {
                    found = min(found, z)
                }
                // println("loop $loop m $minM a $z")
                if (z == prevZ) {
                    break
                }
                prevZ = z
            }
        }
        println(found)
        var il = found
        println("il: $il")
        val a = longToInput(il).toMutableList()
        // assertEquals(0L, v)
        // val a = longToInput(il).toMutableList()
        (0..0).forEach { loop ->
            println("loop $loop")
            for (i in (0..13)) {
                for (j in (0..13)) {
                    if (j == i) {
                        continue
                    }
                    for (k in (0..13)) {
                        if (k in listOf(i, j)) {
                            continue
                        }
                        val ax = a.toMutableList()
                        for (vi in (9L downTo 1)) {
                            for (vj in (9L downTo 1)) {
                                for (vk in (9L downTo 1)) {
                                    ax[i] = vi
                                    ax[j] = vj
                                    ax[k] = vk
                                    val alu = Alu(ax, monad)
                                    alu.run()
                                    val il2 = inputToLong(ax)
                                    if (alu.inspect(z) == 0L && inputToLong(ax) < il) {
                                        il = il2
                                        println(il2)
                                        a[i] = vi
                                        a[j] = vj
                                        a[k] = vk
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        println(il)
        assertEquals(31162141116841, il)
    }
}