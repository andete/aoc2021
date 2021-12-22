package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day22 {
    private val exampleData = """on x=-20..26,y=-36..17,z=-47..7
on x=-20..33,y=-21..23,z=-26..28
on x=-22..28,y=-29..23,z=-38..16
on x=-46..7,y=-6..46,z=-50..-1
on x=-49..1,y=-3..46,z=-24..28
on x=2..47,y=-22..22,z=-23..27
on x=-27..23,y=-28..26,z=-21..29
on x=-39..5,y=-6..47,z=-3..44
on x=-30..21,y=-8..43,z=-13..34
on x=-22..26,y=-27..20,z=-29..19
off x=-48..-32,y=26..41,z=-47..-37
on x=-12..35,y=6..50,z=-50..-2
off x=-48..-32,y=-32..-16,z=-15..-5
on x=-18..26,y=-33..15,z=-7..46
off x=-40..-22,y=-38..-28,z=23..41
on x=-16..35,y=-41..10,z=-47..6
off x=-32..-23,y=11..30,z=-14..3
on x=-49..-5,y=-3..45,z=-29..18
off x=18..30,y=-20..-8,z=-3..13
on x=-41..9,y=-7..43,z=-33..15
on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
on x=967..23432,y=45373..81175,z=27513..53682"""

    private fun parseData(data: String): List<Pair<Boolean, List<Pair<Int, Int>>>> {
        val res = data.lines().filter { it.isNotEmpty() }.map { line ->
            val y = line.split(' ')
            val turnOn = y[0] == "on"
            turnOn to y[1].split(",").map { range ->
                val x = range.split("=")[1].split("..").map {
                    it.toInt()
                }
                x[0] to x[1]

            }
        }
        return res
    }

    private fun parseData2(data: String): List<Pair<Boolean, Range3D>> {
        val res = data.lines().filter { it.isNotEmpty() }.map { line ->
            val y = line.split(' ')
            val turnOn = y[0] == "on"
            val z = y[1].split(",").map { range ->
                val x = range.split("=")[1].split("..").map {
                    it.toLong()
                }
                x[0] to x[1]

            }
            turnOn to Range3D(
                Range(z[0].first, z[0].second),
                Range(z[1].first, z[1].second),
                Range(z[2].first, z[2].second)
            )
        }
        return res
    }

    data class Field(private val data: MutableList<MutableList<MutableList<Int>>> = mutableListOf()) {
        init {
            (0..100).forEach { z ->
                val zList = mutableListOf<MutableList<Int>>()
                data.add(zList)
                (0..100).forEach { y ->
                    val yList = mutableListOf<Int>()
                    zList.add(yList)
                    (0..100).forEach { x ->
                        yList.add(0)
                    }
                }
            }
        }

        fun get(x: Int, y: Int, z: Int) = data[z + 50][y + 50][x + 50]
        fun set(x: Int, y: Int, z: Int) {
            data[z + 50][y + 50][x + 50] = 1
        }

        fun clear(x: Int, y: Int, z: Int) {
            data[z + 50][y + 50][x + 50] = 0
        }

        fun work(turnOn: Boolean, range: List<Pair<Int, Int>>) {
            for (x in range[0].first..range[0].second) {
                if (x in -50..50) {
                    for (y in range[1].first..range[1].second) {
                        if (y in -50..50) {
                            for (z in range[2].first..range[2].second) {
                                if (z in -50..50) {
                                    if (turnOn) {
                                        set(x, y, z)
                                    } else {
                                        clear(x, y, z)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        fun work(input: List<Pair<Boolean, List<Pair<Int, Int>>>>): Field {
            input.forEach { work(it.first, it.second) }
            return this
        }

        val countOn get() = data.sumOf { it.sumOf { it.count { it == 1 } } }
    }

    data class Range(val a: Long, val b: Long) {
        fun overlap(other: Range): Range? {
            val a0 = max(a, other.a)
            val b0 = min(b, other.b)
            if (b0 < a0) {
                return null
            }
            return Range(a0, b0)
        }

        fun contains(other: Range): Boolean {
            return a <= other.a && b >= other.b
        }

        val valid get() = b >= a

        fun splitRemove(other: Range): List<Range> {
            val r0 = Range(a, other.a - 1)
            val r1 = Range(max(other.a, a), min(b, other.b))
            val r2 = Range(other.b + 1, b)
            return listOf(r0, r1, r2).filter { it.valid }
        }

        val size = b - a + 1

        override fun equals(other: Any?): Boolean {
            if (other !is Range) {
                return false
            }
            return a == other.a && b == other.b
        }

        fun inRange(i: Long, i1: Long): Boolean {
            return a >= i && a <= i1 && b >= i && b <= i1
        }
    }

    data class Range3D(val x: Range, val y: Range, val z: Range) {

        override fun equals(other: Any?): Boolean {
            if (other !is Range3D) {
                return false
            }
            return x == other.x && y == other.y && z == other.z
        }

        val size = x.size * y.size * z.size

        fun overlap(other: Range3D): Range3D? {
            val x0 = x.overlap(other.x) ?: return null
            val y0 = y.overlap(other.y) ?: return null
            val z0 = z.overlap(other.z) ?: return null
            return Range3D(x0, y0, z0)
        }

        fun contains(other: Range3D) = x.contains(other.x) && y.contains(other.y) && z.contains(other.z)

        fun splitRemove(other: Range3D): List<Range3D> {
            val res = mutableListOf<Range3D>()
            for (x in x.splitRemove(other.x)) {
                for (y in y.splitRemove(other.y)) {
                    for (z in z.splitRemove(other.z)) {
                        val q = Range3D(x, y, z)
                        if (q.overlap(other) == null) {
                            res.add(q)
                        }
                    }
                }
            }
            return res
        }

        fun inRange(i: Long, i1: Long) = x.inRange(i, i1) && y.inRange(i, i1) && z.inRange(i, i1)


    }

    data class SmartField(var ranges: MutableList<Range3D> = mutableListOf()) {

        private fun turnOn(range: Range3D) {
            val toRemove = mutableListOf<Range3D>()
            for (r in ranges) {
                if (r.contains(range)) {
                    return
                }
                if (range.contains(r)) {
                    toRemove.add(r)
                }
            }
            ranges.removeAll(toRemove)
            ranges = ranges.flatMap { r ->
                if (range.overlap(r) != null) {
                    r.splitRemove(range)
                } else {
                    listOf(r)
                }
            }.toMutableList()
            ranges.add(range)

        }

        private fun turnOff(range: Range3D) {
            ranges = ranges.flatMap { r ->
                if (range.overlap(r) != null) {
                    r.splitRemove(range)
                } else {
                    listOf(r)
                }
            }.toMutableList()
        }

        fun work(turnOn: Boolean, range: Range3D) {
            if (turnOn) {
                turnOn(range)
            } else {
                turnOff(range)
            }
        }

        fun work(input: List<Pair<Boolean, Range3D>>): SmartField {
            input.forEach { work(it.first, it.second) }
            return this
        }

        val countOn get() = ranges.sumOf { it.size }

    }

    @Test
    fun example1() {
        val data = """on x=10..12,y=10..12,z=10..12
on x=11..13,y=11..13,z=11..13
off x=9..11,y=9..11,z=9..11
on x=10..10,y=10..10,z=10..10"""
        val field = Field().work(parseData(data))
        assertEquals(39, field.countOn)
    }

    @Test
    fun example1Alt() {
        val data = """on x=10..12,y=10..12,z=10..12
on x=11..13,y=11..13,z=11..13
off x=9..11,y=9..11,z=9..11
on x=10..10,y=10..10,z=10..10"""
        val field = SmartField().work(parseData2(data))
        assertEquals(39, field.countOn)
    }

    @Test
    fun example2() {
        val field = Field().work(parseData(exampleData))
        assertEquals(590784, field.countOn)
    }

    @Test
    fun example2Alt() {
        val input = parseData2(exampleData)
        val input2 = input.filter {
            it.second.inRange(-50, 50)
        }
        val field = SmartField().work(input2)
        assertEquals(590784, field.countOn)
    }

    @Test
    fun part1() {
        val field = Field().work(parseData(File("../input/input22.txt").readText()))
        println(field.countOn)
        assertEquals(611378, field.countOn)
    }

    private val reboot = """on x=-5..47,y=-31..22,z=-19..33
on x=-44..5,y=-27..21,z=-14..35
on x=-49..-1,y=-11..42,z=-10..38
on x=-20..34,y=-40..6,z=-44..1
off x=26..39,y=40..50,z=-2..11
on x=-41..5,y=-41..6,z=-36..8
off x=-43..-33,y=-45..-28,z=7..25
on x=-33..15,y=-32..19,z=-34..11
off x=35..47,y=-46..-34,z=-11..5
on x=-14..36,y=-6..44,z=-16..29
on x=-57795..-6158,y=29564..72030,z=20435..90618
on x=36731..105352,y=-21140..28532,z=16094..90401
on x=30999..107136,y=-53464..15513,z=8553..71215
on x=13528..83982,y=-99403..-27377,z=-24141..23996
on x=-72682..-12347,y=18159..111354,z=7391..80950
on x=-1060..80757,y=-65301..-20884,z=-103788..-16709
on x=-83015..-9461,y=-72160..-8347,z=-81239..-26856
on x=-52752..22273,y=-49450..9096,z=54442..119054
on x=-29982..40483,y=-108474..-28371,z=-24328..38471
on x=-4958..62750,y=40422..118853,z=-7672..65583
on x=55694..108686,y=-43367..46958,z=-26781..48729
on x=-98497..-18186,y=-63569..3412,z=1232..88485
on x=-726..56291,y=-62629..13224,z=18033..85226
on x=-110886..-34664,y=-81338..-8658,z=8914..63723
on x=-55829..24974,y=-16897..54165,z=-121762..-28058
on x=-65152..-11147,y=22489..91432,z=-58782..1780
on x=-120100..-32970,y=-46592..27473,z=-11695..61039
on x=-18631..37533,y=-124565..-50804,z=-35667..28308
on x=-57817..18248,y=49321..117703,z=5745..55881
on x=14781..98692,y=-1341..70827,z=15753..70151
on x=-34419..55919,y=-19626..40991,z=39015..114138
on x=-60785..11593,y=-56135..2999,z=-95368..-26915
on x=-32178..58085,y=17647..101866,z=-91405..-8878
on x=-53655..12091,y=50097..105568,z=-75335..-4862
on x=-111166..-40997,y=-71714..2688,z=5609..50954
on x=-16602..70118,y=-98693..-44401,z=5197..76897
on x=16383..101554,y=4615..83635,z=-44907..18747
off x=-95822..-15171,y=-19987..48940,z=10804..104439
on x=-89813..-14614,y=16069..88491,z=-3297..45228
on x=41075..99376,y=-20427..49978,z=-52012..13762
on x=-21330..50085,y=-17944..62733,z=-112280..-30197
on x=-16478..35915,y=36008..118594,z=-7885..47086
off x=-98156..-27851,y=-49952..43171,z=-99005..-8456
off x=2032..69770,y=-71013..4824,z=7471..94418
on x=43670..120875,y=-42068..12382,z=-24787..38892
off x=37514..111226,y=-45862..25743,z=-16714..54663
off x=25699..97951,y=-30668..59918,z=-15349..69697
off x=-44271..17935,y=-9516..60759,z=49131..112598
on x=-61695..-5813,y=40978..94975,z=8655..80240
off x=-101086..-9439,y=-7088..67543,z=33935..83858
off x=18020..114017,y=-48931..32606,z=21474..89843
off x=-77139..10506,y=-89994..-18797,z=-80..59318
off x=8476..79288,y=-75520..11602,z=-96624..-24783
on x=-47488..-1262,y=24338..100707,z=16292..72967
off x=-84341..13987,y=2429..92914,z=-90671..-1318
off x=-37810..49457,y=-71013..-7894,z=-105357..-13188
off x=-27365..46395,y=31009..98017,z=15428..76570
off x=-70369..-16548,y=22648..78696,z=-1892..86821
on x=-53470..21291,y=-120233..-33476,z=-44150..38147
off x=-93533..-4276,y=-16170..68771,z=-104985..-24507"""

    @Test
    fun part2example() {
        val field = SmartField().work(parseData2(reboot))
        assertEquals(2758514936282235, field.countOn)
    }


    @Test
    fun part2() {
        val field = SmartField().work(parseData2(File("../input/input22.txt").readText()))
        assertEquals(1214313344725528, field.countOn)
    }

}