import java.util.LinkedList
import java.util.PriorityQueue
import java.util.Queue
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

    data class MatrixPosition(val i: Int, val j: Int) {
        fun up() = MatrixPosition(i - 1, j)
        fun down() = MatrixPosition(i + 1, j)
        fun left() = MatrixPosition(i, j - 1)
        fun right() = MatrixPosition(i, j + 1)
    }

    fun dfs(matrix: List<List<Int>>, visited: MutableSet<MatrixPosition>, previous: MatrixPosition, current: MatrixPosition): Int {
        if (visited.contains(current)) return 0
        visited.add(current)
        val previousVal = matrix[previous.i][previous.j]
        val currentVal = matrix.getOrNull(current.i)?.getOrNull(current.j) ?: return 0
        if (currentVal == 9 || currentVal <= previousVal) return 0
        return 1 + dfs(matrix, visited, current, current.up()) +
            dfs(matrix, visited, current, current.down()) +
            dfs(matrix, visited, current, current.left()) +
            dfs(matrix, visited, current, current.right())
    }

    fun getSizeOfBasin(matrix: List<List<Int>>, start: MatrixPosition): Int {
        if (matrix[start.i][start.j] == 9) return 0
        val queue = LinkedBlockingQueue<Pair<MatrixPosition, MatrixPosition>>()
        val visited = mutableSetOf(start)
        queue.add(Pair(start, start.up()))
        queue.add(Pair(start, start.down()))
        queue.add(Pair(start, start.left()))
        queue.add(Pair(start, start.right()))
        var size = 1
        while (queue.isNotEmpty()) {
            val currentPair = queue.poll()
            val previous = currentPair.first
            val current = currentPair.second
            if (visited.contains(current)) continue
            val previousVal = matrix[previous.i][previous.j]
            val currentVal = matrix.getOrNull(current.i)?.getOrNull(current.j) ?: continue
            if (currentVal <= previousVal) continue
            visited.add(current)
            if (currentVal == 9) continue
            size += 1
            queue.add(Pair(current, current.up()))
            queue.add(Pair(current, current.down()))
            queue.add(Pair(current, current.left()))
            queue.add(Pair(current, current.right()))
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
