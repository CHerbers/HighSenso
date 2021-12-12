package name.herbers.android.highsenso.data

import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class AnswerSheet(
    val collected_at: Int,
    val locale: String,
    val answers: Array<Objects>,
    val sensordata: Array<Objects>
) {

}
