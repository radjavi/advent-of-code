private typealias Map = Array<CharArray>

fun main() {
    fun parse(input: List<String>): Map =
        input.map { it.toCharArray() }.toTypedArray()

    fun Map.stepChar(char: Char): Pair<Map, Boolean> {
        var hasChanged = false
        val newMap = this.map { it.copyOf() }.toTypedArray()
        for (row in this.indices) {
            for (col in this[row].indices) {
                val nextRow = (row + 1) % this.size
                val nextCol = (col + 1) % this[row].size
                if (char == '>' && this[row][col] == '>' && this[row][nextCol] == '.') {
                    newMap[row][nextCol] = '>'
                    newMap[row][col] = '.'
                    hasChanged = true
                } else if (char == 'v' && this[row][col] == 'v' && this[nextRow][col] == '.') {
                    newMap[nextRow][col] = 'v'
                    newMap[row][col] = '.'
                    hasChanged = true
                }
            }
        }
        return newMap to hasChanged
    }

    fun Map.step(): Pair<Map, Boolean> {
        val (steppedEastMap, hasChangedEast) = this.stepChar('>')
        val (steppedSouthMap, hasChangedSouth) = steppedEastMap.stepChar('v')
        return steppedSouthMap to (hasChangedEast || hasChangedSouth)
    }

    fun Map.prettyPrint() =
        this.map { row -> row.map(::print); println() }

    fun part1(input: List<String>): Int {
        val map = parse(input)

        generateSequence(1) { it + 1 }.fold(map) { currentMap, currentStep ->
            val (newMap, hasChanged) = currentMap.step()
            if (!hasChanged) return currentStep
            newMap
        }

        return -1
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58)
    //check(part2(testInput) == 1)

    val input = readInput("Day25")
    println(part1(input))
    println(part2(input))
}
