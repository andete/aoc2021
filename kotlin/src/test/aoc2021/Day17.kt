package test.aoc2021

import org.junit.jupiter.api.Test

class Day17 {

    private data class Position(val x: Int, val y: Int) {
        fun inRect(rect: List<List<Int>>) = x >= rect[0][0] && x <= rect[0][1] && y >= rect[1][0] && y <= rect[1][1]
        fun pastRect(rect: List<List<Int>>) = x > rect[0][1] || y < rect[1][0]

    }

    private val initial = Position(0, 0)

    private fun parseInput(input: String): List<List<Int>> {
        val parts = input.split(" ")
        val xn = parts[2].split(",")[0].split("=")[1].split("..").map { it.toInt() }
        val yn = parts[3].split("=")[1].split("..").map { it.toInt() }
        return listOf(xn, yn)
    }

    private fun testVelocity(rect: List<List<Int>>, x: Int, y: Int): Int? {
        var xVelosity = x
        var yVelosity = y
        var position = initial
        var highest = 0
        while (true) {
            if (position.pastRect(rect)) {
                return null
            }
            if (position.inRect(rect)) {
                return highest
            }
            position = Position(position.x + xVelosity, position.y + yVelosity)
            if (xVelosity > 0) {
                xVelosity -= 1
            }
            yVelosity -= 1
            if (position.y > highest) {
                highest = position.y
            }
        }
    }

    @Test
    fun example1() {
        val input = "target area: x=20..30, y=-10..-5"
        val rect = parseInput(input)
        println(rect)
        var highest = 0
        for (y in 0..10) {
            val m = if (y == 0) { 1 } else { 0 }
            for (x in m..10) {
                testVelocity(rect, x, y)?.let {
                    if (it > highest) {
                        highest = it
                    }
                }
            }
        }
        println(highest)
        assert(45 == highest)
    }

    @Test
    fun part1() {
        val input = "target area: x=94..151, y=-156..-103"
        val rect = parseInput(input)
        var highest = 0
        for (y in 0..300) {
            val m = if (y == 0) { 1 } else { 0 }
            for (x in m..300) {
                testVelocity(rect, x, y)?.let {
                    if (it > highest) {
                        highest = it
                    }
                }
            }
        }
        println(highest)
        assert(45 == highest)
    }

    @Test
    fun example1Part2() {
        val input = "target area: x=20..30, y=-10..-5"
        val rect = parseInput(input)
        println(rect)
        val velocities = mutableListOf<Position>()
        for (y in -30..30) {
            val m = if (y == 0) { 1 } else { 0 }
            for (x in m..30) {
                testVelocity(rect, x, y)?.let {
                    velocities.add(Position(x, y))
                }
            }
        }
        println(velocities)
        assert(112 == velocities.size)
    }

    @Test
    fun part2() {
        val input = "target area: x=94..151, y=-156..-103"
        val rect = parseInput(input)
        println(rect)
        val velocities = mutableListOf<Position>()
        for (y in -200..200) {
            val m = if (y == 0) { 1 } else { 0 }
            for (x in m..200) {
                testVelocity(rect, x, y)?.let {
                    velocities.add(Position(x, y))
                }
            }
        }
        println(velocities.size)
        assert(5059 == velocities.size)
    }
}