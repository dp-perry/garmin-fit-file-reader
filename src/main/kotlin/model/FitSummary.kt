package model

data class FitSummary (
    val avgHeartRate: Int,
    val avgCadence: Int,
    val distanceMiles: Double,
    val distanceMeters: Double,
    val avgPaceMinPerMile: Double,
    val altitudeOverTime: List<Double>,
    val heartRateOverTime: List<Double>,
    val paceChangeOverTime: List<Double>,
)