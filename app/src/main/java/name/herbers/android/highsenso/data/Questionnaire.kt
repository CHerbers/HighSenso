package name.herbers.android.highsenso.data

/**
 * Data class for questionnaires.
 *
 *@project HighSenso
 *@author Herbers
 */
data class Questionnaire(
    val id: Int,
    val name: String,
    val questions: List<Element>
)