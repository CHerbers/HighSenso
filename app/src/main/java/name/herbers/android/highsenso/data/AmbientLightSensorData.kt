package name.herbers.android.highsenso.data

/**
 * Data class for the ambient light [SensorData].
 * Used in [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientLightSensorData(
    override val collected_at: Long,
    var lux: Float,
    override val name: String = "ambientLightSensorData"
): SensorData()
