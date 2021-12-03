fun main() {
    fun part1(input: List<String>): Int {
        var gamma = ""
        var epsilon = ""
        for (i in 0 until input[0].length) {
            var nrZeros = 0
            var nrOnes = 0
            for (row in input) {
                if (row[i] == '0') nrZeros++ else nrOnes++
            }
            gamma += if (nrZeros >= nrOnes) "0" else "1"
            epsilon += if (nrZeros >= nrOnes) "1" else "0"
        }
        return gamma.toInt(2) * epsilon.toInt(2)
    }

    fun part2(input: List<String>): Int {
        var oxygen = 0
        var oxygenList = input
        for (i in 0 until input[0].length) {
            var nrZeros = 0
            var nrOnes = 0
            for (row in oxygenList) {
                if (row[i] == '0') nrZeros++ else nrOnes++
            }
            val keep = if (nrOnes >= nrZeros) '1' else '0'
            val newList = mutableListOf<String>()
            for (row in oxygenList) {
                if (row[i] == keep) newList.add(row)
            }
            if (newList.size == 1) {
                oxygen = newList.single().toInt(2)
                break
            }
            oxygenList = newList
        }

        var co2 = 0
        var co2List = input
        for (i in 0 until input[0].length) {
            var nrZeros = 0
            var nrOnes = 0
            for (row in co2List) {
                if (row[i] == '0') nrZeros++ else nrOnes++
            }
            val keep = if (nrOnes >= nrZeros) '0' else '1'
            val newList = mutableListOf<String>()
            for (row in co2List) {
                if (row[i] == keep) newList.add(row)
            }
            if (newList.size == 1) {
                co2 = newList.single().toInt(2)
                break
            }
            co2List = newList
        }

        return oxygen * co2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
