package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day20 {

    private val example =
        """..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###"""

    data class Image(val pixels: List<MutableList<Int>>, var extendValue: Int = 0) {
        val ySize = pixels.size
        val xSize = pixels[0].size

        fun lit() = pixels.flatten().count { it == 1 }

        fun get(x: Int, y: Int): Int {
            if (x in 0 until xSize && y in 0 until ySize) {
                return pixels[y][x]
            }
            return extendValue
        }

        fun enhance(algorithm: Algorithm): Image {
            val newImageData = mutableListOf<MutableList<Int>>()
            val newXSize = xSize + 2
            val newYSize = ySize + 2
            for (y in 0 until newYSize) {
                val row = mutableListOf<Int>()
                for (x in 0 until newXSize) {
                    row.add(0)
                }
                newImageData.add(row)
            }
            val newExtendValue = if (extendValue == 0) {
                algorithm.data[0]
            } else {
                algorithm.data[511]
            }
            val newImage = Image(newImageData, newExtendValue)
            for (newY in 0 until newYSize) {
                for (newX in 0 until newXSize) {
                    val x = newX - 1
                    val y = newY - 1
                    val algoIndex = listOf(
                        get(x - 1, y - 1),
                        get(x, y - 1),
                        get(x + 1, y - 1),
                        get(x - 1, y),
                        get(x, y),
                        get(x + 1, y),
                        get(x - 1, y + 1),
                        get(x, y + 1),
                        get(x + 1, y + 1)
                    ).map { it.toString() }.joinToString("").toInt(2)
                    newImage.pixels[newY][newX] = algorithm.data[algoIndex]
                }
            }
            return newImage
        }
    }

    data class Algorithm(val data: List<Int>) {
        override fun toString() = data.map { if (it == 0) { "." } else { "#" } }.joinToString("")
    }

    private fun readData(data: String): Pair<Algorithm, Image> {
        val lines = data.lines()
        val algorithm = lines[0].map {
            if (it == '.') {
                0
            } else {
                1
            }
        }
        assertEquals(512, algorithm.size)
        val image = lines.subList(2, lines.size).filter { it.length > 0 }.map {
            it.map {
                if (it == '.') {
                    0
                } else {
                    1
                }
            }.toMutableList()
        }
        println(image)
        return Algorithm(algorithm) to Image(image)
    }

    @Test
    fun example1() {
        val (algorithm, image) = readData(example)
        println(image)
        println("${image.xSize},${image.ySize}")
        val i2 = image.enhance(algorithm)
        println(i2)
        println("${i2.xSize},${i2.ySize}")
        val i3 = i2.enhance(algorithm)
        println(i3)
        println("${i3.xSize},${i3.ySize}")
        println(i3.lit())
        assert(35 == i3.lit())
        var i = i3
        for (x in 0 until 50-2) {
            i = i.enhance(algorithm)
        }
        println(i.lit())
        assert(3351 == i.lit())
    }

    @Test
    fun part1and2() {
        val (algorithm, image) = readData(File("../input/input20.txt").readText())
        println(algorithm)
        println(image)
        println("${image.xSize}, ${image.ySize}")
        val i2 = image.enhance(algorithm)
        println(i2)
        println("${i2.xSize}, ${i2.ySize}")
        val i3 = i2.enhance(algorithm)
        println(i3)
        println(i3.lit())
        assert(5786 == i3.lit())
        var i = i3
        for (x in 0 until 50-2) {
            i = i.enhance(algorithm)
        }
        println(i.lit())
    }
}