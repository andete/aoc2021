package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class A15 {

    val exampleData = """1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581""".split('\n').map { it.toCharArray().map { it.toString().toInt() } }

    data class Cavern(val size: Position, val positions: Map<Position, PositionInfo>) {
        val start get() = positions[Position(0, 0)]!!
        val score get() = start.score!! - start.risk
    }

    data class Position(val x: Int, val y: Int) {
        override fun toString() = "($x,$y)"
        operator fun plus(other: Position) = Position(x + other.x, y + other.y)
        operator fun plus(direction: Direction) = Position(x + direction.offset.x, y + direction.offset.y)
    }

    data class PositionInfo(
        val position: Position,
        val risk: Int,
        val finish: Boolean,
        val neighbours: MutableMap<Direction, PositionInfo>,
        var lowest: Direction? = null,
    ) {
        override fun toString() = "{$position:$risk:$lowest}"

        val lowestNeighbour get() = lowest?.let { neighbours[it] }

        // very inefficient but very neat :)
        val score: Int?
            get() = if (finish) {
                risk
            } else {
                lowestNeighbour?.let { n -> n.score?.let { it + risk } }
            }
    }

    enum class Direction(val offset: Position) {
        Left(Position(-1, 0)),
        Right(Position(1, 0)),
        Up(Position(0, -1)),
        Down(Position(0, 1))
    }

    private fun makeCavern(data: List<List<Int>>): Cavern {
        val positions = mutableMapOf<Position, PositionInfo>()
        val size = Position(data[0].size, data.size)
        // first build up the PositionInfo's
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, risk ->
                val position = Position(x, y)
                val finish = position == Position(size.x - 1, size.y - 1)
                positions[position] = PositionInfo(position, risk, finish, mutableMapOf())
            }
        }
        // then fill in the neighbours
        for (info in positions.values) {
            for (direction in Direction.values()) {
                val neighBourPosition = info.position + direction
                if (neighBourPosition in positions.keys) {
                    info.neighbours[direction] = positions[neighBourPosition]!!
                }
            }
        }
        // left and up from the corner always go to the finish in one step
        positions[Position(size.x - 2, size.y - 1)]!!.lowest = Direction.Right
        positions[Position(size.x - 1, size.y - 2)]!!.lowest = Direction.Down
        return Cavern(size, positions)
    }

    private fun calculateVisitOrder(cavern: Cavern): List<Position> {
        val l = mutableListOf<Position>()
        for (i in (cavern.size.x + cavern.size.y - 4) downTo 0) {
            for (x in 0 until cavern.size.x) {
                for (y in 0 until cavern.size.y) {
                    if (x + y == i) {
                        l.add(Position(x, y))
                    }
                }
            }
        }
        return l
    }

    private fun calculate(cavern: Cavern, visitOrder: List<Position>) {
        for (p in visitOrder) {
            val positionInfo = cavern.positions[p]!!
            positionInfo.lowest = positionInfo.neighbours.filterValues { it.score != null && it.lowestNeighbour != positionInfo }.map {
                it.key to it.value
            }.minByOrNull { it.second.score!! }!!.first
        }
    }

    private fun incrementRow(data: List<Int>, by: Int): List<Int> {
        var res = data
        for (i in 0 until by) {
            res =  res.map { (it % 9) + 1 }
        }
        return res
    }

    @Test
    fun example() {
        val cavern = makeCavern(exampleData)
        val visitOrder = calculateVisitOrder(cavern)
        calculate(cavern, visitOrder)
        println(cavern.score)
        assertEquals(40, cavern.score)
    }

    @Test
    fun part1() {
        val input = File("../input/input15.txt").readLines().map { it.toCharArray().map { it.toString().toInt() } }
        val cavern = makeCavern(input)
        val visitOrder = calculateVisitOrder(cavern)
        calculate(cavern, visitOrder)
        println(cavern.score)
        assertEquals(363, cavern.score)
    }

    @Test
    fun examplePart2() {
        val extendedMap = (0 until 5).flatMap { i ->
            exampleData.map {
                (0 until 5).flatMap { j ->
                    incrementRow(it, i + j)
                }
            }
        }
        val cavern = makeCavern(extendedMap)
        val visitOrder = calculateVisitOrder(cavern)
        calculate(cavern, visitOrder)
        println(cavern.score)
        assertEquals(315, cavern.score)
    }

    @Test
    fun part2() {
        val input = File("../input/input15.txt").readLines().map { it.toCharArray().map { it.toString().toInt() } }
        val extendedMap = (0 until 5).flatMap { i ->
            input.map {
                (0 until 5).flatMap { j ->
                    incrementRow(it, i + j)
                }
            }
        }
        println("starting...")
        val cavern = makeCavern(extendedMap)
        val visitOrder = calculateVisitOrder(cavern)
        calculate(cavern, visitOrder)
        println(cavern.score)
        // second pass is needed to discover a better side road
        calculate(cavern, visitOrder)
        println(cavern.score)
        assertEquals(2835, cavern.score)
    }

}