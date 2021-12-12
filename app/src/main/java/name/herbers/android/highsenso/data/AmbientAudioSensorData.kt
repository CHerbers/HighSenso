package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientAudioSensorData(
    override val name: String,
    override val collectedAt: Long,
    val amplitude: Float
    ): SensorData()
