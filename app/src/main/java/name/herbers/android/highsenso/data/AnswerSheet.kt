package name.herbers.android.highsenso.data

import name.herbers.android.highsenso.database.HighSensoDatabase

/**
 * Data class for answer sheets.
 * Can be stored in the [HighSensoDatabase].
 *
 *@project HighSenso
 *@author Herbers
 */
data class AnswerSheet(
    val id: Int,        //questionnaire id
    val collected_at: Long,
    val answers: List<Answer>,
    val sensor_data: List<SensorData>?,
    val client: Client,
    val locale: String = "de"
)
