package test.aoc2021

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day16 {

    private fun decodeHexCharToBits(c: Char) = c.toString().toInt(16).toString(2).padStart(4, '0')


    private fun hexTobits(hex: String) = Bits(hex.toCharArray().map { decodeHexCharToBits(it) }.joinToString(""))

    data class Bits(var bits: String) {

        fun pop(amount: Int): String {
            val popped = bits.substring(0, amount)
            bits = bits.substring(amount)
            return popped
        }

        fun popInt(amount: Int) = pop(amount).toInt(2)
        fun popBool() = pop(1).toInt() > 0

        override fun toString() = "[${bits.length}:$bits]"

    }

    interface Packet {
        val version: Int
        val packetType: Int
        var length: Int
        val versionSum: Int
        val value: Long
    }

    data class LiteralPacket(
        override val version: Int,
        override val packetType: Int,
        override var length: Int,
        override val value: Long,
        override val versionSum: Int = version
    ) : Packet

    data class OperatorPacket(
        override val version: Int,
        override val packetType: Int,
        override var length: Int,
        val subPackets: List<Packet>,
        var subPacketsLength: Int? = null
    ) : Packet {
        override val versionSum: Int get() = version + subPackets.sumOf { it.versionSum }
        override val value: Long
            get() = when (packetType) {
                0 -> subPackets.sumOf { it.value }
                1 -> subPackets.map { it.value }.foldRight(1) { i, acc -> i * acc }
                2 -> subPackets.minOf { it.value }
                3 -> subPackets.maxOf { it.value }
                5 -> if (subPackets[0].value > subPackets[1].value) { 1 } else { 0 }
                6 -> if (subPackets[0].value < subPackets[1].value) { 1 } else { 0 }
                7 -> if (subPackets[0].value == subPackets[1].value) { 1 } else { 0 }
                else -> throw IllegalStateException()
            }
    }

    private fun decode(input: Bits): Packet {
        val version = input.popInt(3)
        val packetType = input.popInt(3)
        var subPacketLengths: Int? = null
        var length = 6
        if (packetType == 4) {
            // literal
            var next = true
            var num = 0L
            while (next) {
                num = num shl 4
                next = input.popBool()
                num += input.popInt(4)
                length += 5
            }
            return LiteralPacket(version, packetType, length, num)

        } else {
            val lengthType = input.popBool()
            length += 1
            val subPackets = mutableListOf<Packet>()
            if (!lengthType) {
                subPacketLengths = input.popInt(15)
                length += 15
                var lengthRemaining: Int = subPacketLengths
                while (lengthRemaining >= 11) {
                    val newPacket = decode(input)
                    lengthRemaining -= newPacket.length
                    length += newPacket.length
                    subPackets.add(newPacket)
                }
                if (lengthRemaining > 0) {
                    input.pop(lengthRemaining)
                }
            } else {
                val numberOfPackets = input.popInt(11)
                length += 11
                for (i in 0 until numberOfPackets) {
                    val newPacket = decode(input)
                    length += newPacket.length
                    subPackets.add(newPacket)
                }
            }
            return OperatorPacket(version, packetType, length, subPackets, subPacketLengths)
        }
    }

    @Test
    fun example1() {
        val hex = "D2FE28"
        val num = hex.toInt(16)
        val bits = num.toString(2)
        println(bits)
        val version = bits.substring(0, 3).toInt(2)
        println("version: $version")
        val packet = decode(hexTobits(hex))
        println(packet)
        check(packet is LiteralPacket)
        assert(21 == packet.length)
        assert(2021L == packet.value)
    }

    @Test
    fun example2() {
        val hex = "38006F45291200"
        val packet = decode(hexTobits(hex))
        println(packet)
        check(packet is OperatorPacket)
        assert(2 == packet.subPackets.size)
    }

    @Test
    fun example3() {
        val hex = "EE00D40C823060"
        val packet = decode(hexTobits(hex))
        println(packet)
        check(packet is OperatorPacket)
        assert(3 == packet.subPackets.size)
    }

    @Test
    fun example4() {
        val packet = decode(hexTobits("8A004A801A8002F478"))
        assertEquals(16, packet.versionSum)
    }

    @Test
    fun example5() {
        val bits = hexTobits("620080001611562C8802118E34")
        val bits2 = hexTobits("620080001611562C8802118E34")
        val packet = decode(bits2)
        print(packet)
        assert(12 == packet.versionSum)
    }

    @Test
    fun example6() {
        val packet = decode(hexTobits("C0015000016115A2E0802F182340"))
        assert(23 == packet.versionSum)
    }

    @Test
    fun example7() {
        val packet = decode(hexTobits("A0016C880162017C3686B18A3D4780"))
        assert(31 == packet.versionSum)
    }

    @Test
    fun part1and2() {
        val hex = File("../input/input16.txt").readLines().joinToString("")
        val bits = hexTobits(hex)
        val packet = decode(bits)
        println(packet)
        println(packet.versionSum)
        assert(923 == packet.versionSum)
        println(packet.value)
        assert(258888628940L == packet.value)
    }

}