import java.util.concurrent.LinkedBlockingQueue

fun main() {
    fun parse(input: List<String>) =
        input.map { it.map { it.digitToInt() } }

    fun part1(input: List<String>): Int {
        val matrix = parse(input)
        var riskLevels = 0

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                val current = matrix[i][j]
                val up = matrix.getOrNull(i-1)?.get(j)
                val down = matrix.getOrNull(i+1)?.get(j)
                val left = matrix[i].getOrNull(j-1)
                val right = matrix[i].getOrNull(j+1)
                if (up != null && up <= current) continue
                if (down != null && down <= current) continue
                if (left != null && left <= current) continue
                if (right != null && right <= current) continue

                riskLevels += current + 1
            }
        }

        return riskLevels
    }

    fun dfs(matrix: List<List<Int>>, visited: MutableSet<MatrixPosition>, previous: MatrixPosition, current: MatrixPosition): Int {
        if (visited.contains(current)) return 0
        visited.add(current)
        val previousVal = matrix[previous.row][previous.col]
        val currentVal = matrix.getOrNull(current.row)?.getOrNull(current.col) ?: return 0
        if (currentVal == 9 || currentVal <= previousVal) return 0
        return 1 + dfs(matrix, visited, current, current.north()) +
            dfs(matrix, visited, current, current.south()) +
            dfs(matrix, visited, current, current.west()) +
            dfs(matrix, visited, current, current.east())
    }

    fun getSizeOfBasin(matrix: List<List<Int>>, start: MatrixPosition): Int {
        if (matrix[start.row][start.col] == 9) return 0
        val queue = LinkedBlockingQueue<Pair<MatrixPosition, MatrixPosition>>()
        val visited = mutableSetOf(start)
        queue.add(Pair(start, start.north()))
        queue.add(Pair(start, start.south()))
        queue.add(Pair(start, start.west()))
        queue.add(Pair(start, start.east()))
        var size = 1
        while (queue.isNotEmpty()) {
            val currentPair = queue.poll()
            val previous = currentPair.first
            val current = currentPair.second
            if (visited.contains(current)) continue
            val previousVal = matrix[previous.row][previous.col]
            val currentVal = matrix.getOrNull(current.row)?.getOrNull(current.col) ?: continue
            if (currentVal <= previousVal) continue
            visited.add(current)
            if (currentVal == 9) continue
            size += 1
            queue.add(Pair(current, current.north()))
            queue.add(Pair(current, current.south()))
            queue.add(Pair(current, current.west()))
            queue.add(Pair(current, current.east()))
        }
        return size
    }

    fun part2(input: List<String>): Int {
        val matrix = parse(input)
        val basinSizes = mutableListOf<Int>()

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                basinSizes.add(getSizeOfBasin(matrix, MatrixPosition(i, j)))
            }
        }

        return basinSizes.sorted().subList(basinSizes.lastIndex - 2, basinSizes.lastIndex + 1).reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
