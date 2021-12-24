package test.aoc2021

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private fun initialBurrow(): Map<Position, PositionSituation> {
    val positions = mutableMapOf<Position, PositionSituation>()
    for (x in 0..12) {
        for (y in 0..4) {
            val p = Position(x, y)
            positions[p] = Wall(p)
        }
    }
    for (x in 1..11) {
        val p = Position(x, 1)
        positions[p] = MoveStay(p)
    }
    for (x in 3..9 step 2) {
        positions[Position(x, 1)] = Move(Position(x, 1))
    }
    positions[Position(3, 2)] = Destination(Position(3, 2), AmphipodType.Amber)
    positions[Position(3, 3)] = Destination(Position(3, 3), AmphipodType.Amber)
    positions[Position(5, 2)] = Destination(Position(5, 2), AmphipodType.Bronze)
    positions[Position(5, 3)] = Destination(Position(5, 3), AmphipodType.Bronze)
    positions[Position(7, 2)] = Destination(Position(7, 2), AmphipodType.Copper)
    positions[Position(7, 3)] = Destination(Position(7, 3), AmphipodType.Copper)
    positions[Position(9, 2)] = Destination(Position(9, 2), AmphipodType.Desert)
    positions[Position(9, 3)] = Destination(Position(9, 3), AmphipodType.Desert)
    positions.remove(Position(0, 3))
    positions.remove(Position(0, 4))
    positions.remove(Position(1, 3))
    positions.remove(Position(1, 4))
    positions.remove(Position(11, 3))
    positions.remove(Position(11, 4))
    positions.remove(Position(12, 3))
    positions.remove(Position(12, 4))
    return positions
}

private fun initialBurrow2(): Map<Position, PositionSituation> {
    val positions = mutableMapOf<Position, PositionSituation>()
    for (x in 0..12) {
        for (y in 0..6) {
            val p = Position(x, y)
            positions[p] = Wall(p)
        }
    }
    for (x in 1..11) {
        val p = Position(x, 1)
        positions[p] = MoveStay(p)
    }
    for (x in 3..9 step 2) {
        positions[Position(x, 1)] = Move(Position(x, 1))
    }
    positions[Position(3, 2)] = Destination(Position(3, 2), AmphipodType.Amber)
    positions[Position(3, 3)] = Destination(Position(3, 3), AmphipodType.Amber)
    positions[Position(3, 4)] = Destination(Position(3, 4), AmphipodType.Amber)
    positions[Position(3, 5)] = Destination(Position(3, 5), AmphipodType.Amber)
    positions[Position(5, 2)] = Destination(Position(5, 2), AmphipodType.Bronze)
    positions[Position(5, 3)] = Destination(Position(5, 3), AmphipodType.Bronze)
    positions[Position(5, 4)] = Destination(Position(5, 4), AmphipodType.Bronze)
    positions[Position(5, 5)] = Destination(Position(5, 5), AmphipodType.Bronze)
    positions[Position(7, 2)] = Destination(Position(7, 2), AmphipodType.Copper)
    positions[Position(7, 3)] = Destination(Position(7, 3), AmphipodType.Copper)
    positions[Position(7, 4)] = Destination(Position(7, 4), AmphipodType.Copper)
    positions[Position(7, 5)] = Destination(Position(7, 5), AmphipodType.Copper)
    positions[Position(9, 2)] = Destination(Position(9, 2), AmphipodType.Desert)
    positions[Position(9, 3)] = Destination(Position(9, 3), AmphipodType.Desert)
    positions[Position(9, 4)] = Destination(Position(9, 4), AmphipodType.Desert)
    positions[Position(9, 5)] = Destination(Position(9, 5), AmphipodType.Desert)
    listOf(0, 1, 11, 12).forEach { x ->
        listOf(3, 4, 5, 6).forEach { y -> positions.remove(Position(x, y)) }
    }
    return positions
}

private data class Position(val x: Int, val y: Int) {
    fun neighbours(maxY: Int) = listOf(
            Position(x - 1, y),
            Position(x + 1, y),
            Position(x, y - 1),
            Position(x, y + 1)
        ).filter { x >= 0 && y >= 0 && x <= 12 && y <= 1 + maxY }


    override fun toString() = "($x,$y)"
}

private data class Path(val t: AmphipodType, val pn: List<Position>) {
    val start = pn.first()
    val destination = pn.last()
    val energy = (pn.size - 1) * t.energy
}

private enum class AmphipodType(val energy: Int, val c: Char) {
    Amber(1, 'a'),
    Bronze(10, 'b'),
    Copper(100, 'c'),
    Desert(1000, 'd');
}

private abstract class PositionSituation(open val position: Position, open val c: Char) {
    val x get() = position.x
    val y get() = position.y
}

private data class Wall(override val position: Position, override val c: Char = '#') : PositionSituation(position, c)

private data class Move(override val position: Position, override val c: Char = '.') : PositionSituation(position, c)

private data class MoveStay(override val position: Position, override val c: Char = '·') :
    PositionSituation(position, c)

private data class Destination(override val position: Position, val t: AmphipodType, override val c: Char = t.c) :
    PositionSituation(position, c)

private data class SideRoom(val world: World, val p0: Destination, val p1: Destination) {
    val space = world.isEmpty(p0) && world.isEmpty(p1) || world.isEmpty(p0) && world.isFinal(p1)
    val x = p0.x
}

private data class Amphipod(val i: Int, val t: AmphipodType, val positions: List<Position>) {
    val position = positions.lastOrNull()
    fun move(p: Position) = Amphipod(i, t, positions + listOf(p))
    fun move(p: Path) = Amphipod(i, t, positions + p.pn.subList(1, p.pn.size))
    val energy = (positions.size - 1) * t.energy
    val x get() = position!!.x
    val y get() = position!!.y

    override fun toString() = "A($t$i)$positions"
}

private data class World(
    val burrow: Burrow = Burrow(),
    val numA: Int = 2,
    val amphipods: List<Amphipod> = AmphipodType.values()
        .flatMap { t -> (0 until numA).map { i -> Amphipod(i, t, listOf()) } }
) {
    val final
        get() = amphipods.all {
            isFinal(it.position!!)
        }

    val energy = amphipods.sumOf { it.energy }

    fun move(a: Amphipod, p: Path) = World(burrow, numA, amphipods.map {
        if (it == a) {
            a.move(p)
        } else {
            it
        }
    })

    fun identify() = amphipods.map { it.position }.hashCode()

    fun start(l: List<AmphipodType>): World {
        val d = burrow.positions.values.filterIsInstance<Destination>()
        val m = AmphipodType.values().associateWith { 0 }.toMutableMap()
        val a1 = l.map { t ->
            val i = m[t]!!
            m[t] = i + 1
            amphipods.single { it.i == i && it.t == t }
        }.zip(d).map { it.first.move(it.second.position) }
        return World(burrow, numA, a1)
    }

    fun show(): String {
        val maxY = burrow.positions.values.map { it.y }.maxOrNull()!!
        return (0..maxY).map { y ->
            (0..12).map { x ->
                amphipods.firstOrNull { it.position == Position(x, y) }?.t?.c?.uppercaseChar()
                    ?: burrow.positions[Position(x, y)]?.c ?: ' '
            }.joinToString("")
        }.joinToString("\n")
    }

    fun position(x: Int, y: Int): Pair<PositionSituation, Amphipod?> {
        val a = amphipods.firstOrNull { it.position == Position(x, y) }
        val p = burrow.position(x, y)
        return p to a
    }

    fun position(p: Position): Pair<PositionSituation, Amphipod?> {
        val a = amphipods.firstOrNull { it.position == p }
        val s = burrow.position(p.x, p.y)
        return s to a
    }

    fun sideRoom(t: AmphipodType): SideRoom {
        val p = burrow.positions.values.filterIsInstance<Destination>().filter { it.t == t }
        return SideRoom(this, p[0], p[1])
    }

    fun isEmpty(position: Position): Boolean = amphipods.none { it.position == position }
    fun isEmpty(positionSituation: PositionSituation) = isEmpty(positionSituation.position)
    fun isFinal(position: Position) = isFinal(burrow.positions[position]!!)
    fun isFinal(positionSituation: PositionSituation): Boolean {
        val a = amphipods.firstOrNull { it.position == positionSituation.position } ?: return false
        val p = positionSituation as? Destination ?: return false
        return p.t == a.t && burrow.positions.values.filterIsInstance<Destination>()
            .filter { it.t == a.t && it.y > p.y }.all { position(it.position).second?.t == a.t }
    }

    fun possibleMoves(
        a: Amphipod,
        p: Position = a.position!!,
        seen: List<Position> = listOf(p)
    ): List<Pair<Path, Boolean>> {
        if (isFinal(p)) {
            return emptyList()
        }
        val res = mutableListOf<Pair<Path, Boolean>>()
        val original = position(a.position!!).first
        val inHallway = original is MoveStay
        p.neighbours(numA).filter { isEmpty(it) }.filter { it !in seen }.forEach { newP ->
            val s = position(newP).first
            if (s is MoveStay) {
                val newSeen = seen + listOf(newP)
                res.add(Path(a.t, newSeen) to false)
                res.addAll(possibleMoves(a, newP, newSeen))
            } else if (s is Move) {
                val newSeen = seen + listOf(newP)
                res.addAll(possibleMoves(a, newP, newSeen))
            } else if (s is Destination) {
                if (s.t == a.t) {
                    // we always enter a room via y == 2
                    if (s.y == 2 && p.y == 1) {
                        val nextInRoom = position(s.x, s.y + 1)
                        if (numA == 4) {
                            val nextInRoom2 = position(s.x, s.y + 2)
                            val nextInRoom3 = position(s.x, s.y + 3)
                            if (nextInRoom.second == null && nextInRoom2.second == null && nextInRoom3.second == null) {
                                // move all the way down
                                val newSeen = seen + listOf(
                                    newP,
                                    nextInRoom.first.position,
                                    nextInRoom2.first.position,
                                    nextInRoom3.first.position
                                )
                                res.add(Path(a.t, newSeen) to true)
                            }
                            if (nextInRoom.second == null && nextInRoom2.second == null && nextInRoom3.second?.t == a.t) {
                                val newSeen = seen + listOf(newP, nextInRoom.first.position, nextInRoom2.first.position)
                                res.add(Path(a.t, newSeen) to true)
                            }
                            if (nextInRoom.second == null && nextInRoom2.second?.t == a.t && nextInRoom3.second?.t == a.t) {
                                val newSeen = seen + listOf(newP, nextInRoom.first.position)
                                res.add(Path(a.t, newSeen) to true)
                            }
                            if (nextInRoom.second?.t == a.t && nextInRoom2.second?.t == a.t && nextInRoom3.second?.t == a.t) {
                                val newSeen = seen + listOf(newP)
                                res.add(Path(a.t, newSeen) to true)
                            }
                        } else {
                            if (nextInRoom.second != null && nextInRoom.second!!.t == a.t) {
                                val newSeen = seen + listOf(newP)
                                res.add(Path(a.t, newSeen) to true)
                            } else if (nextInRoom.second == null) {
                                // or the bottom room is empty, we have to move into the bottom room
                                val newSeen = seen + listOf(newP, nextInRoom.first.position)
                                res.add(Path(a.t, newSeen) to true)
                            }
                        }
                    } else {
                        // for the larger room, allow moving out as long as there is a wrong one below and the road is clear
                        if (numA == 4) {
                            val anyBelowWrong = ((s.y + 1)..5).any { y ->
                                val q = position(s.x, y).second
                                q == null || q.t != a.t
                            }
                            val allAboveEmpty = ((s.y - 1)..2).all { y -> position(s.x, y).second == null }
                            if (anyBelowWrong && allAboveEmpty) {
                                val newSeen = seen + listOf(newP)
                                res.addAll(possibleMoves(a, newP, newSeen))
                            }
                        }
                    }
                } else {
                    // only when moving out
                    if (original is Destination && newP.y < original.y) {
                        val newSeen = seen + listOf(newP)
                        res.addAll(possibleMoves(a, newP, newSeen))
                    }
                }
            }
        }
        // if we have a move to a final position, that is always the only move!
        res.firstOrNull {
            it.second
        }?.let { return listOf(it) }
        // only go to final room from hallway!
        if (inHallway) {
            return res.filter { position(it.first.destination).first is Destination }
        }
        return res
    }
}

private data class Burrow(val score: Int = 0, val positions: Map<Position, PositionSituation> = initialBurrow()) {
    fun position(x: Int, y: Int) = positions[Position(x, y)]!!
}

private fun calculate(world: World, seen: List<World> = listOf()): World? {
    if (world.final) {
        return world
    }
    val res = mutableListOf<World>()
    val seenI = seen.map { it.identify() }
    val possibleMoves = world.amphipods.map { it to world.possibleMoves(it) }.filter { it.second.isNotEmpty() }
    val directFinishes = possibleMoves.filter {
        it.second.size == 1 && it.second[0].second
    }
    // we _always_ first want to do all direct finishes
    if (directFinishes.isNotEmpty()) {
        return calculate(world.move(directFinishes[0].first, directFinishes[0].second[0].first), seen + listOf(world))
    }

    val otherMoves = possibleMoves.filter { !it.second[0].second }
    // and only then try the others
    for (other in otherMoves) {
        val (a, r) = other
        for (path in r) {
            val newW2 = world.move(a, path.first)
            if (newW2.identify() in seenI || newW2.identify() == world.identify()) {
                continue
            }
            calculate(newW2, seen + listOf(world))?.let {
                res.add(it)
            }
        }
    }
    return res.minByOrNull { it.energy }
}

class Day23 {

    @Test
    fun example1() {
        var b = World()
        // b.show()
        println()
        b = b.start(
            listOf(
                AmphipodType.Bronze,
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Desert,
                AmphipodType.Bronze,
                AmphipodType.Copper,
                AmphipodType.Desert,
                AmphipodType.Amber
            )
        )
        b.show()
        println(b.possibleMoves(b.amphipods[0]))
        println(b.possibleMoves(b.amphipods[1]))
        val l = calculate(b)!!
        println(l.energy)
        assertEquals(12521, l.energy)
    }

    @Test
    fun part1() {
        var b = World()
        b = b.start(
            listOf(
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Desert,
                AmphipodType.Desert,
                AmphipodType.Copper,
                AmphipodType.Bronze,
                AmphipodType.Amber,
                AmphipodType.Bronze
            )
        )
        println(b.show())
        val l = calculate(b)!!
        println(l.energy)
        assertEquals(15365, l.energy)
    }

    @Test
    fun example2() {
        var w = World(Burrow(0, initialBurrow2()), 4)
        w = w.start(
            listOf(
                AmphipodType.Bronze,
                AmphipodType.Desert,
                AmphipodType.Desert,
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Copper,
                AmphipodType.Bronze,
                AmphipodType.Desert,
                AmphipodType.Bronze,
                AmphipodType.Bronze,
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Desert,
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Amber
            )
        )
        println(w.show())
        w = calculate(w)!!
        println(w.energy)
        assertEquals(44169, w.energy)
    }

    @Test
    fun part2() {
        var w = World(Burrow(0, initialBurrow2()), 4)
        w = w.start(
            listOf(
                AmphipodType.Amber,
                AmphipodType.Desert,
                AmphipodType.Desert,
                AmphipodType.Copper,
                AmphipodType.Desert,
                AmphipodType.Copper,
                AmphipodType.Bronze,
                AmphipodType.Desert,
                AmphipodType.Copper,
                AmphipodType.Bronze,
                AmphipodType.Amber,
                AmphipodType.Bronze,
                AmphipodType.Amber,
                AmphipodType.Amber,
                AmphipodType.Copper,
                AmphipodType.Bronze
            )
        )
        println(w.show())
        w = calculate(w)!!
        println(w.energy)
    }
}
