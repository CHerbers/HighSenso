package name.herbers.android.highsenso.data

/**
 * Data class for the answers.
 * Used in [Questionnaire]s and [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class Answer(
    val value: String,
    val label: String? = null,
    val collected_at: Long? = null
)
