package test.aoc2021

import org.junit.jupiter.api.Test
import private.Coordinate
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.test.assertEquals

class Day19 {

    private val orientations = listOf(0.0, 90.0, 180.0, 270.0)

    private data class Scanner(val index: Int, var location: IntCoordinate?, val beacons: Set<IntCoordinate>) {
    }

    private data class IntCoordinate(
        val x: Int = 0,
        val y: Int = 0,
        val z: Int = 0,
        val w: Int = 0,
        val p: Int = 0,
        val r: Int = 0
    ) {
        constructor(a: Coordinate) : this(
            a.x.roundToInt(),
            a.y.roundToInt(),
            a.z.roundToInt(),
            a.w.roundToInt(),
            a.p.roundToInt(),
            a.r.roundToInt()
        )

        fun convert() = Coordinate(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble(), p.toDouble(), r.toDouble())
    }

    private fun parseFile(filename: String): List<Scanner> {

        return File(filename).readText().split("---").filterIndexed { index, s -> index > 0 && index % 2 == 0 }
            .map { scanner ->
                scanner.lines().filter { it.isNotBlank() }
                    .map { it.split(",").filter { it.isNotEmpty() }.map { it.toInt() } }
            }.mapIndexed { index, scanner ->
                Scanner(
                    index,
                    if (index == 0) {
                        IntCoordinate()
                    } else {
                        null
                    },
                    scanner.map { IntCoordinate(it[0], it[1], it[2]) }.toSet()
                )
            }
    }

    private fun discoverOverlap(scanner: Scanner, other: Scanner) {
        val knownBeacons = scanner.beacons.map { it.convert().fromReferenceToWorld(scanner.location!!.convert()) }
        val knownBeaconsXyz = knownBeacons.map { IntCoordinate(it.xyz()) }
        for (knownBeaconLocationAbsolute in knownBeacons) {
            for (unknownBeaconLocationRelative in other.beacons) {
                for (w in orientations) {
                    for (p in orientations) {
                        for (r in orientations) {
                            val rotation = Coordinate(w = w, p = p, r = r)
                            // take all orientations of unknownBeaconLocationRelative
                            val unknownBeaconLocationRelativeRotated =
                                unknownBeaconLocationRelative.convert().fromWorldToReference(rotation)
                            // take the offset compared to knownBeaconLocationAbsolute
                            // next line is messy and is an indication the the rotation is perhaps applied wrongly above
                            val possibleScannerLocation =
                                ((knownBeaconLocationAbsolute.xyz() - unknownBeaconLocationRelativeRotated.xyz()) + unknownBeaconLocationRelativeRotated.orientation())
                            // then calculate the rotated and offsetted location for each beacon of the other
                            val newLocations = other.beacons.map {
                                IntCoordinate(
                                    it.convert().fromReferenceToWorld(possibleScannerLocation).xyz()
                                )
                            }.toSet()
                            val overlapping = newLocations.intersect(knownBeaconsXyz)
                            if (overlapping.size >= 12) {
                                other.location = IntCoordinate(possibleScannerLocation)
                                println("Scanner ${other.index} located at ${other.location}")
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun example1() {
        val data = parseFile("../input/input19-ex1.txt")
        discoverOverlap(data[0], data[1])
        assertEquals(IntCoordinate(68, -1246, -43, w = 180, r = -180), data[1].location)
        // locations of scanner0 beacons relative to location of scanner1
        discoverOverlap(data[1], data[4])
        assertEquals(IntCoordinate(-20, -1133, 1061, w = 0, p = -90, r = 90), data[4].location)
        discoverOverlap(data[4], data[2])
        assertEquals(IntCoordinate(1105, -1205, 1229, w = 90, p = 0, r = -180), data[2].location)
        discoverOverlap(data[1], data[3])
        assertEquals(IntCoordinate(-92, -2380, -20, w = 180, p = 0, r = -180), data[3].location)
    }

    @Test
    fun example2() {
        val data = parseFile("C:/Users/joost.damad/aoc/aoc2021/input/input19-ex1.txt")
        for (scanner0 in data) {
            for (scanner1 in data) {
                if (scanner0.location != null && scanner1.location == null) {
                    discoverOverlap(scanner0, scanner1)
                }
            }
        }

        val beacons = mutableSetOf<IntCoordinate>()
        for (scanner in data) {
            for (beacon in scanner.beacons) {
                beacons.add(IntCoordinate(beacon.convert().fromReferenceToWorld(scanner.location!!.convert()).xyz()))
            }
        }
        println(beacons)
        println(beacons.size)
        assertEquals(79, beacons.size)
    }

    @Test
    fun part1and2() {
        val data = parseFile("C:/Users/joost.damad/aoc/aoc2021/input/input19.txt")
        while (data.any { it.location == null }) {
            for (scanner0 in data) {
                for (scanner1 in data) {
                    if (scanner0.location != null && scanner1.location == null) {
                        discoverOverlap(scanner0, scanner1)
                    }
                }
            }
        }
        val beacons = mutableSetOf<IntCoordinate>()
        for (scanner in data) {
            for (beacon in scanner.beacons) {
                beacons.add(IntCoordinate(beacon.convert().fromReferenceToWorld(scanner.location!!.convert()).xyz()))
            }
        }
        println(beacons)
        println(beacons.size)
        assertEquals(353, beacons.size)
        var maxManhattan = 0
        for (scanner0 in data) {
            for (scanner1 in data) {
                val d =
                    (scanner0.location!!.x - scanner1.location!!.x).absoluteValue + (scanner0.location!!.y - scanner1.location!!.y).absoluteValue + (scanner0.location!!.z - scanner1.location!!.z).absoluteValue
                if (d > maxManhattan) {
                    maxManhattan = d
                }
            }
        }
        println(maxManhattan)
    }
}