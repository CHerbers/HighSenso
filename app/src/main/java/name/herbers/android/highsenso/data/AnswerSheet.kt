package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AnswerSheet(
    val id: Int,
    val collected_at: Long,
    val locale: String = "de",
    val answers: List<Answer>,
    val sensorData: List<SensorData>,
    val client: Client
)
