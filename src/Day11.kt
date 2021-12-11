data class Octopus(var energyLevel: Int, val neighbours: List<MatrixPosition>)

fun main() {
    fun getMatrix(input: List<String>) =
        input.map { row -> row.map { col -> col.digitToInt() } }

    fun getListOfOctopus(input: List<String>): MutableMap<MatrixPosition, Octopus> {
        val matrix = getMatrix(input)
        val positionToOctopus = mutableMapOf<MatrixPosition, Octopus>()

        for (row in matrix.indices) {
            for (col in matrix[row].indices) {
                val position = MatrixPosition(row, col)
                val energyLevel = matrix[row][col]
                val potentialNeighbours = listOf(
                    position.north(),
                    position.south(),
                    position.west(),
                    position.east(),
                    position.northeast(),
                    position.southeast(),
                    position.southwest(),
                    position.northwest(),
                )
                val neighbours = potentialNeighbours.mapNotNull {
                    if (it.row in matrix.indices && it.col in matrix[row].indices) it else null
                }
                positionToOctopus[position] = Octopus(energyLevel, neighbours)
            }
        }

        return positionToOctopus
    }

    fun countFlashes(positionToOctopus: MutableMap<MatrixPosition, Octopus>): Int {
        for ((position, octopus) in positionToOctopus.toMap()) {
            positionToOctopus[position] = octopus.apply { energyLevel += 1 }
        }

        val flashed = mutableSetOf<MatrixPosition>()
        val octopusWithHighEnergy =
            ArrayDeque(positionToOctopus.mapNotNull { (pos, octopus) ->
                if (octopus.energyLevel > 9) pos else null
            })
        while (octopusWithHighEnergy.isNotEmpty()) {
            val position = octopusWithHighEnergy.removeFirst()
            if (flashed.contains(position)) continue
            flashed.add(position)
            for (neighbour in positionToOctopus[position]!!.neighbours) {
                positionToOctopus[neighbour] = positionToOctopus[neighbour]!!.apply { energyLevel += 1 }
                if (positionToOctopus[neighbour]!!.energyLevel > 9 && !flashed.contains(neighbour)) {
                    octopusWithHighEnergy.addLast(neighbour)
                }
            }
        }
        for ((position, octopus) in positionToOctopus.toMap()) {
            if (octopus.energyLevel > 9) {
                positionToOctopus[position] = positionToOctopus[position]!!.apply { energyLevel = 0 }
            }
        }
        return flashed.size
    }

    fun part1(input: List<String>): Int {
        val positionToOctopus = getListOfOctopus(input)
        var flashes = 0

        for (step in 1..100) {
            flashes += countFlashes(positionToOctopus)
        }

        return flashes
    }

    fun part2(input: List<String>): Int {
        val positionToOctopus = getListOfOctopus(input)

        for (step in generateSequence(1) { it + 1 }) {
            if (countFlashes(positionToOctopus) == 100) {
                return step
            }
        }

        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    //check(part2(testInput) == 1)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
