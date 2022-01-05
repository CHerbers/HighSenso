package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientLightSensorData(
    override val collectedAt: Long,
    var lux: Float,
    override val name: String = "ambientLightSensorData"
): SensorData()
