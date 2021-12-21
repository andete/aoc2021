package test.aoc2021

import org.junit.jupiter.api.Test
import java.lang.Integer.min
import kotlin.test.assertEquals

private val dirac3 = (1..3).flatMap { a ->
    (1..3).flatMap { b ->
        (1..3).map { c ->
            listOf(a, b, c).sum()
        }
    }
}

private var p1Wins = 0L
private var p2Wins = 0L
private var winsCache = mutableMapOf<List<Int>, Pair<Long, Long>>()

class Day21 {

    data class DeterministicDice(val maxNum: Int = 100, var i: Int = 1) {
        private var _rolls = 0

        val rolls get() = _rolls

        fun roll(): Int {
            ++_rolls
            val res = i
            i = (i % maxNum) + 1
            return res
        }

        fun multiRoll(t: Int = 3) = (0 until t).sumOf { roll() }
    }

    data class PlayerBoard(
        val initialPosition: Int,
        val boardSize: Int = 10,
        var position: Int = initialPosition,
        var score: Int = 0
    ) {
        fun play(d: DeterministicDice) {
            move(d.multiRoll())
            score += position
        }

        fun move(m: Int) {
            position = ((position - 1 + m) % boardSize) + 1
        }
    }

    data class DiracBoard(
        val initialPosition: Int,
        val boardSize: Int = 10,
        var position: Int = initialPosition,
        var score: Int = 0
    ) {
        fun play(): List<DiracBoard> {
            val d1 = copy().move(1)
            d1.score += d1.position
            val d2 = copy().move(2)
            d2.score += d2.position
            val d3 = copy().move(3)
            d3.score += d3.position
            return listOf(d1, d2, d3)
        }

        fun move(m: Int): DiracBoard {
            position = ((position - 1 + m) % boardSize) + 1
            return this
        }
    }

    data class Universe(val p1Board: DiracBoard, val p2Board: DiracBoard) {
        fun play1(): List<Universe> {
            val newU = dirac3.map {
                val p1BoardA = p1Board.copy().move(it)
                p1BoardA.score += p1BoardA.position
                p1BoardA
            }.map { Universe(it, p2Board) }
            p1Wins += newU.count { it.p1Board.score >= 21 }
            return newU.filter { it.p1Board.score < 21 }
        }

        fun play2(): List<Universe> {
            val newU = dirac3.map {
                val p2BoardA = p2Board.copy().move(it)
                p2BoardA.score += p2BoardA.position
                p2BoardA
            }.map { Universe(it, p2Board) }
            p2Wins += newU.count { it.p2Board.score >= 21 }
            return newU.filter { it.p2Board.score < 21 }
        }
    }

    private fun count(pos1: Int, pos2: Int, score1: Int, score2: Int): Pair<Long, Long> {
        val key = listOf(pos1, pos2, score1, score2)
        winsCache[key]?.let {
            p1Wins += it.first
            p2Wins += it.second
            return it
        }
        var p1WinsHere = 0L
        var p2WinsHere = 0L
        for (roll1 in dirac3) {
            val newPos1 = (pos1 -1 + roll1) % 10 + 1
            val newScore1 = score1 + newPos1
            if (newScore1 >= 21) {
                p1Wins++
                p1WinsHere++
                continue
            } else {
                for (roll2 in dirac3) {
                    val newPos2 = (pos2 -1 + roll2) % 10 + 1
                    val newScore2 = score2 + newPos2
                    if (newScore2 >= 21) {
                        p2Wins ++
                        p2WinsHere ++
                    } else {
                        val (x, y) = count(newPos1, newPos2, newScore1, newScore2)
                        p1WinsHere += x
                        p2WinsHere += y
                    }
                }
            }
        }
        winsCache[key] = p1WinsHere to p2WinsHere
        return p1WinsHere to p2WinsHere
    }

    @Test
    fun example1() {
        val d = DeterministicDice()
        val p1 = PlayerBoard(4)
        val p2 = PlayerBoard(8)
        p1.play(d)
        assertEquals(10, p1.score)
        p2.play(d)
        assertEquals(3, p2.score)
        p1.play(d)
        assertEquals(14, p1.score)
        p2.play(d)
        assertEquals(9, p2.score)
        p1.play(d)
        assertEquals(20, p1.score)
        p2.play(d)
        assertEquals(16, p2.score)
        p1.play(d)
        assertEquals(26, p1.score)
        p2.play(d)
        assertEquals(22, p2.score)
    }

    @Test
    fun example2() {
        val d = DeterministicDice()
        val p1 = PlayerBoard(4)
        val p2 = PlayerBoard(8)
        while (p1.score < 1000 && p2.score < 1000) {
            p1.play(d)
            if (p1.score >= 1000) {
                break
            }
            p2.play(d)
        }
        val losing = min(p1.score, p2.score)
        val rolls = d.rolls
        assertEquals(745, losing)
        assertEquals(993, rolls)
    }

    @Test
    fun part1() {
        val d = DeterministicDice()
        val p1 = PlayerBoard(1)
        val p2 = PlayerBoard(10)
        while (p1.score < 1000 && p2.score < 1000) {
            p1.play(d)
            if (p1.score >= 1000) {
                break
            }
            p2.play(d)
        }
        val losing = min(p1.score, p2.score)
        val rolls = d.rolls
        val res = losing * rolls
        println(res)
    }

    @Test
    fun example2part2() {
        var universes = listOf(Universe(DiracBoard(4), DiracBoard(8)))
        while (universes.isNotEmpty()) {
            universes = universes.flatMap { u1 ->
                val newU = u1.play1()
                newU.flatMap {
                    it.play2()
                }
            }
        }
        assertEquals(444356092776315L, p1Wins)
        assertEquals(341960390180808L, p2Wins)
    }

    @Test
    fun example2part2simplified() {
        p1Wins = 0
        p2Wins = 0
        count(4, 8, 0, 0)
        assertEquals(444356092776315L, p1Wins)
        assertEquals(341960390180808L, p2Wins)
    }
    @Test
    fun part2() {
        p1Wins = 0
        p2Wins = 0
        count(1, 10, 0, 0)
        println(p1Wins)
        println(p2Wins)
        assertEquals(57328067654557, p1Wins)
        assertEquals(32971421421058, p2Wins)
    }
}