package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private interface SnailFishNumber {
    var parent: SnailFishNumber?
    val magnitude: Int
}

private data class SimpleNumber(var value: Int, override var parent: SnailFishNumber? = null) : SnailFishNumber {
    override fun toString() = value.toString()
    override val magnitude get() = value
}

private class SnailFishNumberIterator(private val pairNumber: PairNumber) : Iterator<SnailFishNumber> {
    private var iteratorPositionLeft = true
    private var finished = false

    override fun hasNext() = !finished

    override fun next(): SnailFishNumber {
        if (iteratorPositionLeft) {
            iteratorPositionLeft = false
            return pairNumber.left
        } else {
            finished = true
            return pairNumber.right
        }
    }
}

private data class PairNumber(
    var l: Pair<SnailFishNumber, SnailFishNumber>?,
    override var parent: SnailFishNumber? = null
) : SnailFishNumber, Iterable<SnailFishNumber> {
    val p get() = l!!
    val left get() = p.first
    val right get() = p.second
    override fun toString() = "[$left,$right]"
    override fun iterator(): Iterator<SnailFishNumber> = SnailFishNumberIterator(this)

    operator fun plus(other: PairNumber): PairNumber {
        val newNumber = PairNumber(this to other)
        this.parent = newNumber
        other.parent = newNumber
        return simplify(newNumber) as PairNumber
    }

    fun basicPlus(other: PairNumber): PairNumber {
        val newNumber = PairNumber(this.copy() to other.copy())
        this.parent = newNumber
        other.parent = newNumber
        return newNumber
    }
    override val magnitude get() = 3 * left.magnitude + 2 * right.magnitude
}

private fun parse(
    number: String,
    positionIn: Int = 0,
    parent: SnailFishNumber? = null
): Pair<SnailFishNumber, Int> {
    var position = positionIn
    if (number.isEmpty()) {
        throw IllegalStateException()
    }
    val c = number[position]
    ++position
    if (c == '[') {
        val pairNumber = PairNumber(null, parent)
        val l = mutableListOf<SnailFishNumber>()
        val (left, newPosition) = parse(number, position, pairNumber)
        position = newPosition
        ++position // ,
        val (right, newPosition2) = parse(number, position, pairNumber)
        position = newPosition2
        ++position // ]
        pairNumber.l = left to right
        return pairNumber to position
    }
    --position
    var s: String = ""
    while (number[position] in listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
        s += number[position]
        ++position
    }
    return SimpleNumber(s.toInt(), parent) to position
}

private fun find(
    number: SnailFishNumber,
    depth: Int = 0,
    action: (SnailFishNumber, Int) -> Boolean
): SnailFishNumber? {
    if (number is SimpleNumber) {
        if (action(number, depth)) {
            return number
        } else {
            return null
        }
    } else if (number is PairNumber) {
        if (action(number, depth)) {
            return number
        }
        for (n2 in number) {
            val r = find(n2, depth + 1, action)
            if (r != null) {
                return r
            }
        }
        return null
    } else {
        throw IllegalStateException()
    }
}

private fun lineairNumbers(number: SnailFishNumber): List<SimpleNumber> {
    if (number is SimpleNumber) {
        return listOf(number)
    } else if (number is PairNumber) {
        return lineairNumbers(number.left) + lineairNumbers(number.right)
    } else {
        throw IllegalStateException()
    }
}

private fun explode(number: SnailFishNumber): SnailFishNumber? {
    find(number, 0) { s, depth ->
        depth == 4 && s is PairNumber
    }?.let { pairNumber ->
        pairNumber as PairNumber
        check(pairNumber.left is SimpleNumber)
        check(pairNumber.right is SimpleNumber)
        val l = lineairNumbers(number)
        var leftIndex = 0
        // indexOf doesn't work because normal equality (==) gives false positives
        l.forEachIndexed { index, simpleNumber ->
            if (simpleNumber === pairNumber.left) {
                leftIndex = index
            }
        }
        val rightIndex = leftIndex + 1
        val lineairLeft = if (leftIndex == 0) {
            null
        } else {
            l[leftIndex - 1]
        }
        val lineairRight = if (rightIndex == l.size - 1) {
            null
        } else {
            l[rightIndex + 1]
        }
        lineairLeft?.let {
            lineairLeft.value += (pairNumber.left as SimpleNumber).value
        }
        lineairRight?.let {
            lineairRight.value += (pairNumber.right as SimpleNumber).value
        }
        val parent = pairNumber.parent!! as PairNumber
        if (parent.left == pairNumber) {
            parent.l = SimpleNumber(0, parent) to parent.right
        } else {
            parent.l = parent.left to SimpleNumber(0, parent)
        }
        return number
    }
    return null
}

private fun split(number: SnailFishNumber): SnailFishNumber? {
    find(number, 0) { s, depth ->
        s is SimpleNumber && s.value >= 10
    }?.let { simpleNumber ->
        check(simpleNumber is SimpleNumber)
        val parent = simpleNumber.parent as PairNumber
        val n1 = simpleNumber.value / 2
        val n2 = (simpleNumber.value + 1) / 2
        val newNumber = PairNumber(null, parent)
        val n1Number = SimpleNumber(n1, newNumber)
        val n2Number = SimpleNumber(n2, newNumber)
        newNumber.l = n1Number to n2Number
        if (parent.left == simpleNumber) {
            parent.l = newNumber to parent.right
        } else {
            parent.l = parent.left to newNumber
        }
        return number
    }
    return null
}

private fun simplify(number: SnailFishNumber): SnailFishNumber {
    var n = number
    var changed = true
    while (changed) {
        changed = false
        explode(n)?.let {
            n = it
            changed = true
        } ?: split(n)?.let {
            n = it
            changed = true
        }
    }
    return n
}

class Day18 {

    @Test
    fun example1() {
        val number = "[[[[[9,8],1],2],3],4]"
        val p = parse(number).first
        assertEquals(number, p.toString())
        assertEquals("[9, 8, 1, 2, 3, 4]", lineairNumbers(p).toString())
        for (i in p as PairNumber) {
            println(i)
        }
        assertEquals("[[[[9,8],1],2],3]", (p.left as PairNumber).left.parent.toString())
        val p2 = explode(p)
        assertEquals("[[[[0,9],2],3],4]", p2.toString())
    }

    @Test
    fun example2() {
        val number = "[7,[6,[5,[4,[3,2]]]]]"
        val p = parse(number).first
        assertEquals(number, p.toString())
        val p2 = explode(p as PairNumber)
        assertEquals("[7,[6,[5,[7,0]]]]", p2.toString())
    }

    @Test
    fun example3() {
        val number = "[[6,[5,[4,[3,2]]]],1]"
        val p = parse(number).first
        assertEquals(number, p.toString())
        val p2 = explode(p as PairNumber)
        assertEquals("[[6,[5,[7,0]]],3]", p2.toString())
    }

    @Test
    fun example4() {
        val number = "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]"
        val p = parse(number).first
        assertEquals(number, p.toString())
        val p2 = explode(p)!!
        assertEquals("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", p2.toString())
    }

    @Test
    fun example5() {
        val number = "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"
        val p = parse(number).first
        assertEquals(number, p.toString())
        val p2 = explode(p)!!
        assertEquals("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", p2.toString())
    }

    @Test
    fun example6() {
        val n1 = parse("[[[[4,3],4],4],[7,[[8,4],9]]]").first as PairNumber
        val n2 = parse("[1,1]").first as PairNumber
        val n = n1.basicPlus(n2)
        assertEquals("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]", n.toString())
        val e1 = explode(n)!!
        assertEquals("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]", e1.toString())
        val e2 = explode(e1)!!
        assertEquals("[[[[0,7],4],[15,[0,13]]],[1,1]]", e2.toString())
        val s1 = split(e2)!!
        assertEquals("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]", s1.toString())
        val s2 = split(s1)!!
        assertEquals("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]", s2.toString())
        val e3 = explode(s2)!!
        assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", e3.toString())
    }

    @Test
    fun example7() {
        val n1 = parse("[[[[4,3],4],4],[7,[[8,4],9]]]").first as PairNumber
        val n2 = parse("[1,1]").first as PairNumber
        val n = n1 + n2
        assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", n.toString())
    }

    @Test
    fun example8() {
        val data = """[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
[7,[5,[[3,8],[1,4]]]]
[[2,[2,2]],[8,[8,1]]]
[2,9]
[1,[[[9,3],9],[[9,0],[0,7]]]]
[[[5,[7,4]],7],1]
[[[[4,2],2],6],[8,7]]""".lines().map { parse(it).first as PairNumber }
        val res = data.subList(1, data.size).fold(data[0]) { acc, pairNumber -> acc + pairNumber }
        assertEquals("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", res.toString())
    }

    @Test
    fun example9() {
       assertEquals(143, parse("[[1,2],[[3,4],5]]").first.magnitude)
    }

    @Test
    fun example10() {
        val data = """[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]""".lines().map { parse(it).first as PairNumber }
        val res = data.subList(1, data.size).fold(data[0]) { acc, pairNumber -> acc + pairNumber }
        assertEquals(4140, res.magnitude)
        var m = 0
        for (i1 in data) {
            for (i2 in data) {
                val x = (i1 + i2).magnitude
                if (x > m) {
                    m = x
                }
            }
        }
        println(m)
    }

    @Test
    fun part1() {
        val data = File("../input/input18.txt").readLines().map { parse(it).first as PairNumber }
        val res = data.subList(1, data.size).fold(data[0]) { acc, pairNumber -> acc + pairNumber }
        println(res.magnitude)
    }
    @Test
    fun part2() {
        var m = 0
        for (i in 0 until 100) {
            for (j in 0 until 100) {
                // unfortunately my + operator doesn't take deep copies so it has side effects
                // simple workaround: re-read data every single time
                val data = File("../input/input18.txt").readLines().map { parse(it).first as PairNumber }
                val x = (data[i] + data[j]).magnitude
                if (x > m) {
                    m = x
                }
            }
        }
        println(m)
    }
}