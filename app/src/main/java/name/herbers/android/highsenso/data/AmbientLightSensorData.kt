package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientLightSensorData(
    override val name: String,
    override val collectedAt: Long,
    var lux: Float
): SensorData()
