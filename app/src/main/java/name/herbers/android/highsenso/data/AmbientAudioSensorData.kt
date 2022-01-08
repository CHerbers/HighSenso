package name.herbers.android.highsenso.data

/**
 * Data class for the ambient audio [SensorData].
 * Used in [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class AmbientAudioSensorData(
    override val collected_at: Long,
    val amplitude: Float,
    override val name: String = "ambientAudioSensorData"
): SensorData()
