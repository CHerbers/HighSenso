package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientTemperatureSensorData(
    override val name: String,
    override val collectedAt: Long,
    val degreesCelsius: Float
): SensorData()
