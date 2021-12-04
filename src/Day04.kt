import java.lang.RuntimeException

fun main() {
    fun part1(input: List<String>): Int {
        val numbersToDraw = input[0].split(",").map { it.toInt() }

        val boards = input.slice(2 until input.size)
            .windowed(size = 5, step = 6)
            .map { board ->
                board.map { row ->
                    row.trim().split("\\s+".toRegex()).map { it.toInt() }
                }
            }

        val drawnNumbers = mutableSetOf<Int>()
        var winningBoard: List<List<Int>> = emptyList()
        var winningDrawnNumber = -1
        outer@ for (drawnNumber in numbersToDraw) {
            drawnNumbers.add(drawnNumber)
            for (board in boards) {
                for (row in board.indices) {
                    if (board[row].intersect(drawnNumbers) == board[row].toSet()) {
                        winningBoard = board
                        winningDrawnNumber = drawnNumber
                        break@outer
                    }
                }
                for (col in board.indices) {
                    val colNumbers = mutableListOf<Int>()
                    for (row in board.indices) {
                        colNumbers.add(board[row][col])
                    }
                    if (colNumbers.intersect(drawnNumbers) == colNumbers.toSet()) {
                        winningBoard = board
                        winningDrawnNumber = drawnNumber
                        break@outer
                    }
                }
            }
        }

        if (winningDrawnNumber == -1) throw RuntimeException("No winning drawn number")

        var sumUnmarked = 0
        for (row in winningBoard.indices) {
            for (col in winningBoard.indices) {
                if (!drawnNumbers.contains(winningBoard[row][col])) sumUnmarked += winningBoard[row][col]
            }
        }

        return sumUnmarked * winningDrawnNumber
    }

    fun part2(input: List<String>): Int {
        val numbersToDraw = input[0].split(",").map { it.toInt() }

        val boards = input.slice(2 until input.size)
            .windowed(size = 5, step = 6)
            .map { board ->
                board.map { row ->
                    row.trim().split("\\s+".toRegex()).map { it.toInt() }
                }
            }

        val drawnNumbers = mutableSetOf<Int>()
        var lastWinningBoard: List<List<Int>> = emptyList()
        var lastWinningDrawnNumber = -1
        val setOfWinningBoards = mutableSetOf<Int>()
        outer@ for (drawnNumber in numbersToDraw) {
            drawnNumbers.add(drawnNumber)
            for ((index, board) in boards.withIndex()) {
                for (row in board.indices) {
                    if (board[row].intersect(drawnNumbers) == board[row].toSet()) {
                        if (setOfWinningBoards.size == boards.size - 1 && !setOfWinningBoards.contains(index)) {
                            lastWinningBoard = board
                            lastWinningDrawnNumber = drawnNumber
                            break@outer
                        }
                        setOfWinningBoards.add(index)
                    }
                }
                for (col in board.indices) {
                    val colNumbers = mutableListOf<Int>()
                    for (row in board.indices) {
                        colNumbers.add(board[row][col])
                    }
                    if (colNumbers.intersect(drawnNumbers) == colNumbers.toSet()) {
                        if (setOfWinningBoards.size == boards.size - 1 && !setOfWinningBoards.contains(index)) {
                            lastWinningBoard = board
                            lastWinningDrawnNumber = drawnNumber
                            break@outer
                        }
                        setOfWinningBoards.add(index)
                    }
                }
            }
        }

        if (lastWinningDrawnNumber == -1) throw RuntimeException("No winning drawn number")

        var sumUnmarked = 0
        for (row in lastWinningBoard.indices) {
            for (col in lastWinningBoard.indices) {
                if (!drawnNumbers.contains(lastWinningBoard[row][col])) sumUnmarked += lastWinningBoard[row][col]
            }
        }

        return sumUnmarked * lastWinningDrawnNumber
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
