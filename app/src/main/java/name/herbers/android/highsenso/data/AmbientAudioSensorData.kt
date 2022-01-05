package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientAudioSensorData(
    override val collectedAt: Long,
    val amplitude: Float,
    override val name: String = "ambientAudioSensorData"
): SensorData()
