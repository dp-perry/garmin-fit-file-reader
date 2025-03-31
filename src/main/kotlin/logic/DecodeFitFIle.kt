package logic

import com.garmin.fit.*
import model.FitSummary
import java.io.File
import java.nio.file.Files

fun decodeFitFile(file: File): FitSummary? {
    val heartRates = mutableListOf<Double>()
    val cadences = mutableListOf<Int>()
    val altitudes = mutableListOf<Double>()
    val paces = mutableListOf<Double>()

    var totalDistance = 0.0
    var totalHeartRate = 0
    var totalCadence = 0
    var hrCount = 0
    var cadenceCount = 0

    try{

        val bytes = Files.readAllBytes(file.toPath())
        val decoder = Decoder(bytes)
        val broadcaster = MesgBroadcaster()

        val recordListener = object : RecordMesgListener {
            override fun onMesg(mesg: RecordMesg) {
                println("RECORD: HR=${mesg.heartRate}, Speed=${mesg.speed}")

                mesg.heartRate?.let {
                    heartRates.add(it.toDouble())
                    totalHeartRate += it
                    hrCount++
                }
                mesg.cadence?.let {
                    cadences.add(it.toInt())
                    totalCadence += it
                    cadenceCount++
                }
                mesg.enhancedAltitude?.let {
                    altitudes.add(it.toDouble())
                }

                mesg.enhancedSpeed?.let { speed ->
                    if (speed > 0) {
                        val pace = (1 / speed) * 1000 / 60 // min/km
                        paces.add(pace * 1.60934) // convert to min/mi
                    }
                }
                mesg.distance?.let {
                    totalDistance = it.toDouble()
                }

            }
        }

        broadcaster.addListener(recordListener)

        decoder.addListener(broadcaster as MesgListener)
        decoder.addListener(broadcaster as MesgDefinitionListener)
        decoder.read()

        val avgHR = if (hrCount > 0) totalHeartRate / hrCount else 0
        val avgCadence = if (cadenceCount > 0) totalCadence / cadenceCount else 0
        val miles = totalDistance / 1609.34
        val avgPace = if (paces.isNotEmpty()) paces.average() else 0.0

        return FitSummary(
            avgHeartRate = avgHR,
            avgCadence = avgCadence,
            distanceMeters = totalDistance,
            distanceMiles = miles,
            avgPaceMinPerMile = avgPace,
            altitudeOverTime = altitudes,
            heartRateOverTime = heartRates,
            paceChangeOverTime = paces
        )
    } catch (e: Exception) {
        println("Error decoding FIT file: ${e.message}")
    }

    return null
}