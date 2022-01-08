package name.herbers.android.highsenso.data

/**
 * Data class for the ambient temperature [SensorData].
 * Used in [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientTempSensorData(
    override val collected_at: Long,
    val degreesCelsius: Float,
    override val name: String = "ambientTemperatureSensorData"
): SensorData()
