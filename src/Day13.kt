fun main() {
    fun getDots(input: List<String>) =
        input.takeWhile { it.firstOrNull()?.isDigit() ?: false }
            .map { it.split(",").let { (x, y) -> MatrixPosition(y.toInt(), x.toInt()) } }.toSet()

    fun getFolds(input: List<String>) =
        input.takeLastWhile { it.startsWith("fold along") }
            .map { it.split("fold along ")[1].split("=").let { it[0][0] to it[1].toInt() } }

    fun prettyPrint(dots: Set<MatrixPosition>) {
        val maxRow = dots.maxByOrNull { it.row }!!.row
        val minRow = dots.minByOrNull { it.row }!!.row
        val maxCol = dots.maxByOrNull { it.col }!!.col
        val minCol = dots.minByOrNull { it.col }!!.col
        for (row in minRow..maxRow) {
            for (col in minCol..maxCol) {
                if (dots.contains(MatrixPosition(row, col))) print("#") else print(".")
            }
            println()
        }
    }

    fun foldVerticallyAndGetDots(dots: Set<MatrixPosition>, foldAt: Int): Set<MatrixPosition> {
        val dotsAbove = dots.filter { it.row < foldAt }
        val dotsBelow = dots.filter { it.row > foldAt }
        val newDots = dotsAbove.toMutableSet()
        for (dot in dotsBelow) {
            newDots.add(
                MatrixPosition(
                    row = foldAt - (dot.row - foldAt),
                    col = dot.col,
                )
            )
        }
        return newDots
    }

    fun foldHorizontallyAndGetDots(dots: Set<MatrixPosition>, foldAt: Int): Set<MatrixPosition> {
        val dotsLeft = dots.filter { it.col < foldAt }
        val dotsRight = dots.filter { it.col > foldAt }
        val newDots = dotsLeft.toMutableSet()
        for (dot in dotsRight) {
            newDots.add(
                MatrixPosition(
                    row = dot.row,
                    col = foldAt - (dot.col - foldAt),
                )
            )
        }
        return newDots
    }

    fun foldAndGetDots(dots: Set<MatrixPosition>, foldAt: Pair<Char, Int>) =
        if (foldAt.first == 'x') foldHorizontallyAndGetDots(dots, foldAt.second)
        else foldVerticallyAndGetDots(dots, foldAt.second)

    fun part1(input: List<String>): Int {
        val dots = getDots(input)
        val folds = getFolds(input)
        val firstFold = folds[0]

        return foldAndGetDots(dots, firstFold).size
    }

    fun part2(input: List<String>): Int {
        val dots = getDots(input)
        val folds = getFolds(input)

        val folded = folds.fold(dots) { newDots, foldAt -> foldAndGetDots(newDots, foldAt) }
        prettyPrint(folded)
        return folded.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    //check(part2(testInput) == 1)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
