interface Packet {
    val version: Int
    val typeId: Int
}

data class LiteralPacket(override val version: Int, override val typeId: Int, val value: Long) : Packet
data class OperatorPacket(
    override val version: Int,
    override val typeId: Int,
    val lengthTypeId: Int,
    val subPackets: List<Packet>
) : Packet

fun hexToBinary(input: List<String>) =
    input[0].map { char -> char.digitToInt(16).toString(2).let { "0".repeat(4 - it.length) + it } }
        .joinToString("")

fun parseLiteralValue(subpacket: String): Pair<Long, String> {
    var currentIndex = 0
    var valueAsBinaryString = ""
    while (subpacket[currentIndex] == '1') {
        valueAsBinaryString += subpacket.substring(currentIndex + 1 until currentIndex + 5)
        currentIndex += 5
    }
    valueAsBinaryString += subpacket.substring(currentIndex + 1 until currentIndex + 5)
    return valueAsBinaryString.toLong(2) to subpacket.substring((currentIndex + 5)..subpacket.lastIndex)
}

fun parsePacket(packet: String): Pair<Packet, String>? {
    if (packet.length < 6) return null
    var version = packet.substring(0..2).toInt(2)
    var typeId = packet.substring(3..5).toInt(2)

    // Literal
    if (typeId == 4) {
        val (literalValue, remainingPacket) = parseLiteralValue(packet.substring(6..packet.lastIndex))
        return LiteralPacket(version = version, typeId = typeId, value = literalValue) to remainingPacket
    }

    // Operator
    var lengthTypeId = packet[6].digitToInt(2)
    val lengthOfSubPacket = if (lengthTypeId == 0) packet.substring(7 until (7 + 15)).toInt(2) else null
    val numberOfSubPackets = if (lengthTypeId == 1) packet.substring(7 until (7 + 11)).toInt(2) else Int.MAX_VALUE
    var remainingSubPacket =
        lengthOfSubPacket?.let { packet.substring((7 + 15) until (7 + 15 + it)) } ?: packet.substring(
            (7 + 11)..packet.lastIndex
        )
    var currentSubPacket = 1

    val subPackets = mutableListOf<Packet>()
    while (currentSubPacket <= numberOfSubPackets) {
        val (subpacket, remaining) = parsePacket(remainingSubPacket) ?: break
        subPackets.add(subpacket)
        remainingSubPacket = remaining
        currentSubPacket++
    }
    var remainingPacket = lengthOfSubPacket?.let { packet.substring((7 + 15 + it)..packet.lastIndex) } ?: remainingSubPacket
    return OperatorPacket(
        version = version,
        typeId = typeId,
        lengthTypeId = lengthTypeId,
        subPackets = subPackets
    ) to remainingPacket
}

fun sumOfVersions(packet: Packet): Int {
    if (packet is LiteralPacket) return packet.version
    return packet.version + (packet as OperatorPacket).subPackets.sumOf { sumOfVersions(it) }
}

fun calculateValue(packet: Packet): Long {
    if (packet is LiteralPacket) return packet.value
    val operatorPacket = packet as OperatorPacket
    return when (operatorPacket.typeId) {
        0 -> operatorPacket.subPackets.sumOf { calculateValue(it) }
        1 -> operatorPacket.subPackets.fold(1) { value, p -> value * calculateValue(p) }
        2 -> operatorPacket.subPackets.minOf { calculateValue(it) }
        3 -> operatorPacket.subPackets.maxOf { calculateValue(it) }
        5 -> if (calculateValue(operatorPacket.subPackets[0]) > calculateValue(operatorPacket.subPackets[1])) 1 else 0
        6 -> if (calculateValue(operatorPacket.subPackets[0]) < calculateValue(operatorPacket.subPackets[1])) 1 else 0
        7 -> if (calculateValue(operatorPacket.subPackets[0]) == calculateValue(operatorPacket.subPackets[1])) 1 else 0
        else -> throw IllegalArgumentException()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val binary = hexToBinary(input)
        val (packet, _) = parsePacket(binary) ?: return -1

        return sumOfVersions(packet)
    }

    fun part2(input: List<String>): Long {
        val binary = hexToBinary(input)
        val (packet, _) = parsePacket(binary) ?: return -1

        return calculateValue(packet)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    //check(part1(testInput) == 16)
    check(part2(testInput) == 1L)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
