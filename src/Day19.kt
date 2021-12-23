import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun main() {
    data class Beacon(val x: Int, val y: Int, val z: Int)
    data class Scanner(val beacons: MutableSet<Beacon>)

    val beaconComparator = compareBy<Beacon> { it.x }.thenBy { it.y }.thenBy { it.z }

    fun parse(input: List<String>): List<Scanner> {
        val splitted = mutableListOf<List<String>>()
        var currentStart = 1
        for (i in input.indices) {
            if (input[i].isBlank()) {
                splitted.add(
                    input.subList(currentStart, i)
                )
                currentStart = i + 2
            }
        }
        splitted.add(
            input.subList(currentStart, input.size)
        )

        return splitted.map { scanner ->
            Scanner(
                scanner.map {
                    it.split(",").let { (x, y, z) -> Beacon(x.toInt(), y.toInt(), z.toInt()) }
                }.toMutableSet()
            )
        }
    }

    fun Scanner.getOrientations() = sequence {
        val angles = listOf(
            Math.toRadians(0.0),
            Math.toRadians(90.0),
            Math.toRadians(180.0),
            Math.toRadians(270.0),
        )
        val rotated = mutableSetOf<Scanner>()
        for (angle in angles) {
            // Rotate along x-axis
            val rotatedX = Scanner(
                beacons.map {
                    Beacon(
                        it.x,
                        (it.y * cos(angle) - it.z * sin(angle)).roundToInt(),
                        (it.y * sin(angle) + it.z * cos(angle)).roundToInt()
                    )
                }.toMutableSet()
            )
            rotated.add(rotatedX)
            yield(rotatedX)
        }
        for (rotatedScanner in rotated.toSet()) {
            for (angle in angles) {
                // Rotate along y-axis
                val rotatedY = Scanner(
                    rotatedScanner.beacons.map {
                        Beacon(
                            (it.x * cos(angle) + it.z * sin(angle)).roundToInt(),
                            it.y,
                            (-it.x * sin(angle) + it.z * cos(angle)).roundToInt()
                        )
                    }.toMutableSet()
                )
                rotated.add(rotatedY)
                yield(rotatedY)
            }
        }
        for (rotatedScanner in rotated.toSet()) {
            for (angle in angles) {
                // Rotate along z-axis
                val rotatedZ = Scanner(
                    rotatedScanner.beacons.map {
                        Beacon(
                            (it.x * cos(angle) - it.y * sin(angle)).roundToInt(),
                            (it.x * sin(angle) + it.y * cos(angle)).roundToInt(),
                            it.z
                        )
                    }.toMutableSet()
                )
                rotated.add(rotatedZ)
                yield(rotatedZ)
            }
        }
    }

    fun Scanner.viewFromBeacon(beacon: Beacon) =
        beacons.map { Beacon(it.x - beacon.x, it.y - beacon.y, it.z - beacon.z) }.toSet()

    fun Scanner.tryOrientationsAndMatchWith(scannerToMatchWith: Scanner): Triple<Scanner, Beacon, Beacon>? {
        for (rotated in this.getOrientations()) {
            for (scannerToMatchWithBeacon in scannerToMatchWith.beacons) {
                val scannerToMatchWithBeaconView = scannerToMatchWith.viewFromBeacon(scannerToMatchWithBeacon)
                for (beacon in rotated.beacons) {
                    val rotatedView = rotated.viewFromBeacon(beacon)
                    val intersection = rotatedView.intersect(scannerToMatchWithBeaconView)
                    if (intersection.size >= 12) return Triple(rotated, beacon, scannerToMatchWithBeacon)
                }
            }
        }
        return null
    }

    fun positionRelativeToReference(
        matchingBeacon: Beacon,
        scannerToMatchWithBeacon: Beacon,
        scannerToMatchWithCoordinate: Coordinate3D,
    ): Coordinate3D {
        return Coordinate3D(
            scannerToMatchWithCoordinate.x + (scannerToMatchWithBeacon.x - matchingBeacon.x),
            scannerToMatchWithCoordinate.y + (scannerToMatchWithBeacon.y - matchingBeacon.y),
            scannerToMatchWithCoordinate.z + (scannerToMatchWithBeacon.z - matchingBeacon.z),
        )
    }

    fun buildMap(
        scanners: MutableList<Scanner>,
        uniqueBeacons: MutableSet<Beacon>,
        scannersToTry: LinkedBlockingQueue<Int>,
        scannersMatched: LinkedBlockingQueue<Int>,
        relativeScannerCoordinates: MutableMap<Int, Coordinate3D>,
    ) {
        outer@ while (scannersToTry.isNotEmpty()) {
            println(scannersToTry)
            for (scannerMatched in scannersMatched) {
                for (scannerToTry in scannersToTry) {
                    // Check 12 beacons
                    val (matchingScanner, matchingBeacon, scannerToMatchWithBeacon) = scanners[scannerToTry].tryOrientationsAndMatchWith(
                        scanners[scannerMatched]
                    ) ?: continue

                    // Add to uniqueBeacons the 12 beacons relative to scannerToMatchWith
                    val matchingScannerRelativePosition = positionRelativeToReference(
                        matchingBeacon = matchingBeacon,
                        scannerToMatchWithBeacon = scannerToMatchWithBeacon,
                        scannerToMatchWithCoordinate = relativeScannerCoordinates[scannerMatched]!!
                    )
                    relativeScannerCoordinates[scannerToTry] = matchingScannerRelativePosition
                    uniqueBeacons.addAll(
                        matchingScanner.beacons.map {
                            Beacon(
                                matchingScannerRelativePosition.x + it.x,
                                matchingScannerRelativePosition.y + it.y,
                                matchingScannerRelativePosition.z + it.z,
                            )
                        }
                    )
                    scannersToTry.remove(scannerToTry)
                    scannersMatched.add(scannerToTry)
                    scanners[scannerToTry] = matchingScanner
                    continue@outer
                }
            }
            throw IllegalStateException("No scanner matched: $scannersToTry")
        }
    }

    fun part1(input: List<String>): Int {
        val scanners = parse(input).toMutableList()

        val uniqueBeacons = mutableSetOf<Beacon>()
        uniqueBeacons.addAll(scanners[0].beacons)
        val scannersMatched = LinkedBlockingQueue<Int>()
        scannersMatched.add(0)
        val scannersToTry = LinkedBlockingQueue<Int>()
        scannersToTry.addAll(1..scanners.lastIndex)
        val relativeScannerCoordinates = mutableMapOf(0 to Coordinate3D(0, 0, 0))

        buildMap(
            scanners = scanners,
            uniqueBeacons = uniqueBeacons,
            scannersToTry = scannersToTry,
            scannersMatched = scannersMatched,
            relativeScannerCoordinates = relativeScannerCoordinates,
        )

        return uniqueBeacons.size
    }

    fun part2(input: List<String>): Int {
        val scanners = parse(input).toMutableList()

        val uniqueBeacons = mutableSetOf<Beacon>()
        uniqueBeacons.addAll(scanners[0].beacons)
        val scannersMatched = LinkedBlockingQueue<Int>()
        scannersMatched.add(0)
        val scannersToTry = LinkedBlockingQueue<Int>()
        scannersToTry.addAll(1..scanners.lastIndex)
        val relativeScannerCoordinates = mutableMapOf(0 to Coordinate3D(0, 0, 0))

        buildMap(
            scanners = scanners,
            uniqueBeacons = uniqueBeacons,
            scannersToTry = scannersToTry,
            scannersMatched = scannersMatched,
            relativeScannerCoordinates = relativeScannerCoordinates,
        )

        var maxDistance = -1
        for (c1 in relativeScannerCoordinates.values) {
            for (c2 in relativeScannerCoordinates.values) {
                val distance = abs(c2.x - c1.x) + abs(c2.y - c1.y) + abs(c2.z - c1.z)
                if (distance > maxDistance) maxDistance = distance
            }
        }

        return maxDistance
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    //check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day19")
    //println(part1(input))
    println(part2(input))
}
