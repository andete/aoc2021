package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day25 {

    private val example = """...>...
.......
......>
v.....>
......>
.......
..vvv.."""

    private val example2 = """v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>"""

    private data class Position(var c: Char)

    private data class Map(val positions: List<List<Position>>) {
        val xSize = positions[0].size
        val ySize = positions.size

        fun position(x: Int, y: Int): Position {
            val modX = (x + xSize) % xSize
            val modY = (y + ySize) % ySize
            return positions[modY][modX]
        }

        override fun toString(): String {
            return positions.joinToString("\n") { it.map { it.c }.joinToString("") }
        }

        fun cycle(): Boolean {
            val movesRight = mutableListOf<Pair<Int, Int>>()
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    val c = position(x, y)
                    val d = position(x + 1, y)
                    if (c.c == '>' && d.c == '.') {
                        movesRight.add(x to y)
                    }
                }
            }
            movesRight.forEach { (x, y) ->
                position(x, y).c = '.'
                position(x + 1, y).c = '>'
            }
            val movesDown = mutableListOf<Pair<Int, Int>>()
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    val c = position(x, y)
                    val d = position(x , y + 1)
                    if (c.c == 'v' && d.c == '.') {
                        movesDown.add(x to y)
                    }
                }
            }
            movesDown.forEach { (x, y) ->
                position(x, y).c = '.'
                position(x, y + 1).c = 'v'
            }
            return movesRight.isNotEmpty() || movesDown.isNotEmpty()
        }

        fun tillNotChanging(): Int {
            var i = 1
            while (cycle()) {
//                println(i)
//                println(this)
//                println()
                i++
            }
            return i
        }
    }

    private fun parse(s: String) =
        Map(s.lines().filter { it.isNotEmpty() }.map {
            it.split("").filter { it.isNotEmpty() }.map { Position(it[0]) }
        })


    @Test
    fun example1() {
        val m = parse(example)
        m.cycle()
        println(m)
    }

    @Test
    fun example2() {
        val m = parse(example2)
        val i = m.tillNotChanging()
        println(i)
        assertEquals(58, i)
    }

    @Test
    fun part1() {
        val m = parse(File("../input/input25.txt").readText())
        val i = m.tillNotChanging()
        println(i)
        assertEquals(417, i)
    }
}