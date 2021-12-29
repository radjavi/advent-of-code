private typealias RebootStep = Pair<Boolean, Cuboid>

private class MergableCuboid {
    private val pieces = mutableSetOf<Cuboid>()

    fun add(cuboid: Cuboid) {
        var cuboids = setOf(cuboid)
        for (piece in pieces.toSet()) {
            val newCuboids = mutableSetOf<Cuboid>()
            for (currentCuboid in cuboids) {
                val intersection = currentCuboid.intersect(piece)
                if (intersection == null) {
                    newCuboids.add(currentCuboid)
                    continue
                }
                if (intersection == currentCuboid) continue
                if (intersection == piece) {
                    newCuboids.add(currentCuboid)
                    pieces.remove(piece)
                    continue
                }
                val splitted = currentCuboid.split(intersection)
                require(splitted.sumOf { it.size } == currentCuboid.size - intersection.size)
                newCuboids.addAll(splitted)
            }
            cuboids = newCuboids
        }
        pieces.addAll(cuboids)
    }

    fun remove(cuboid: Cuboid) {
        val newCuboids = mutableListOf<Cuboid>()
        for (piece in pieces.toSet()) {
            val intersection = cuboid.intersect(piece) ?: continue
            pieces.remove(piece)
            if (intersection == piece) continue
            val splitted = piece.split(intersection)
            require(splitted.sumOf { it.size } == piece.size - intersection.size)
            newCuboids.addAll(splitted)
        }
        pieces.addAll(newCuboids)
    }

    fun size() = pieces.sumOf { it.size }

    private fun Cuboid.split(intersection: Cuboid): List<Cuboid> {
        val newCuboids = mutableListOf<Cuboid>()

        if (x.from < intersection.x.from) {
            newCuboids.add(Cuboid(Range(x.from, intersection.x.from - 1), y, z))
        }
        if (intersection.x.to < x.to) {
            newCuboids.add(Cuboid(Range(intersection.x.to + 1, x.to), y, z))
        }

        if (y.from < intersection.y.from) {
            newCuboids.add(Cuboid(intersection.x, Range(y.from, intersection.y.from - 1), z))
        }
        if (intersection.y.to < y.to) {
            newCuboids.add(Cuboid(intersection.x, Range(intersection.y.to + 1, y.to), z))
        }

        if (z.from < intersection.z.from) {
            newCuboids.add(Cuboid(intersection.x, intersection.y, Range(z.from, intersection.z.from - 1)))
        }
        if (intersection.z.to < z.to) {
            newCuboids.add(Cuboid(intersection.x, intersection.y, Range(intersection.z.to + 1, z.to)))
        }

        return newCuboids
    }
}

fun main() {
    fun parse(input: List<String>): List<RebootStep> =
        input.map { row ->
            val (turnOn, right) = row.split(" ")
            val (xRange, yRange, zRange) = right.split(",").map {
                val (from, to) = Regex("-*\\d+").findAll(it).map { match -> match.value.toInt() }.toList()
                Range(from, to)
            }
            (turnOn == "on") to Cuboid(xRange, yRange, zRange)
        }

    fun List<RebootStep>.countCuboidsOn(): Long {
        val mergableCuboid = MergableCuboid()
        map { (turnOn, cuboid) -> if (turnOn) mergableCuboid.add(cuboid) else mergableCuboid.remove(cuboid) }
        return mergableCuboid.size()
    }

    fun part1(input: List<String>) =
        parse(input).filter { (_, cuboid) ->
            listOf(cuboid.x.from, cuboid.x.to, cuboid.y.from, cuboid.y.to, cuboid.z.from, cuboid.z.to).all {
                it in -50..50
            }
        }.countCuboidsOn()

    fun part2(input: List<String>) =
        parse(input).countCuboidsOn()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 474140L) { "${part1(testInput)} != 474140" }
    check(part2(testInput) == 2758514936282235L) { "${part2(testInput)} != 2758514936282235" }

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
