package name.herbers.android.highsenso.data

/**
 * Data class for sensor data.
 * Inherited by [AmbientAudioSensorData], [AmbientLightSensorData] and [AmbientTempSensorData].
 * Used in [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
abstract class SensorData{
    abstract val name: String
    abstract val collectedAt: Long
}
