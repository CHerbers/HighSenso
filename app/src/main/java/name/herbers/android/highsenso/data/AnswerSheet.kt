package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AnswerSheet(
    val collected_at: Long,
    val locale: String,
    val answers: List<Answer>,
    val sensorData: List<SensorData>,
    val client: Client
)
