package name.herbers.android.highsenso.data

import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class QuestionData(
    val question: String,
    val answers: Array<Objects>,
    val label: String,
    val questiontype: String,
    val values: Array<Objects>
)
