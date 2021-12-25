fun main() {
    fun Char.pixelToInt(): Int =
        if (this == '#') 1 else 0

    fun parse(input: List<String>): Pair<String, List<List<MatrixPosition>>> {
        val enhancementAlgorithm = input[0]
        val rawInputImage = input.subList(2, input.size)
        val image = mutableListOf<MutableList<MatrixPosition>>()
        for (row in rawInputImage.indices) {
            val rowList = mutableListOf<MatrixPosition>()
            for (col in rawInputImage[row].indices) {
                rowList.add(
                    MatrixPosition(row, col).apply {
                        weight = rawInputImage[row][col].pixelToInt()
                    }
                )
            }
            image.add(rowList)
        }
        return enhancementAlgorithm to image
    }

    fun MatrixPosition.enhance(
        fullImage: List<List<MatrixPosition>>,
        enhancementAlgorithm: String,
        defaultPixelValue: Int
    ): MatrixPosition {
        var binaryString = ""
        val pixels = listOf(
            this.northwest(),
            this.north(),
            this.northeast(),
            this.west(),
            this,
            this.east(),
            this.southwest(),
            this.south(),
            this.southeast()
        )
        for ((x, y) in pixels) {
            binaryString += fullImage.getOrNull(x)?.getOrNull(y)?.weight ?: defaultPixelValue
        }
        return this.copy().apply { weight = enhancementAlgorithm[binaryString.toInt(2)].pixelToInt() }
    }

    fun List<List<MatrixPosition>>.enhance(enhancementAlgorithm: String, defaultPixelValue: Int): List<List<MatrixPosition>> {
        val paddedImage = mutableListOf<MutableList<MatrixPosition>>()
        for (row in -2..(this.lastIndex + 2)) {
            val rowList = mutableListOf<MatrixPosition>()
            for (col in -2..(this[0].lastIndex + 2)) {
                rowList.add(
                    MatrixPosition(row + 2, col + 2).apply {
                        weight = this@enhance.getOrNull(row)?.getOrNull(col)?.weight ?: defaultPixelValue
                    }
                )
            }
            paddedImage.add(rowList)
        }
        return paddedImage.map { row -> row.map { it.enhance(paddedImage, enhancementAlgorithm, defaultPixelValue) } }
    }

    fun List<List<MatrixPosition>>.printPixelString() =
        this.map { row -> row.map { if (it.weight > 0) print('#') else print('.') }; println() }

    fun part1(input: List<String>): Int {
        val (enhancementAlgorithm, image) = parse(input)
        val firstEnhancedImage = image.enhance(enhancementAlgorithm, 0)
        val secondEnhancedImage = firstEnhancedImage.enhance(enhancementAlgorithm, firstEnhancedImage[0][0].weight)
        return secondEnhancedImage.sumOf { row -> row.count { it.weight > 0 } }
    }

    fun part2(input: List<String>): Int {
        val (enhancementAlgorithm, image) = parse(input)
        val enhancedImage = (1..50).fold(image) { newImage, i -> newImage.enhance(enhancementAlgorithm, if (i == 1) 0 else newImage[0][0].weight) }
        return enhancedImage.sumOf { row -> row.count { it.weight > 0 } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
