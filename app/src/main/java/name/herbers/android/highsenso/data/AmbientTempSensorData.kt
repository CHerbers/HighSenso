package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientTempSensorData(
    override val collectedAt: Long,
    val degreesCelsius: Float,
    override val name: String = "ambientTemperatureSensorData"
): SensorData()
