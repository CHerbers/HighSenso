package name.herbers.android.highsenso.data

import name.herbers.android.highsenso.Constants

/**
 * Data class for a question.
 * Used by [Questionnaire]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class Question(
    override val position: Int,
    val name: String,
    val questiontype: String,
    val label: String,
    val values: Any,                  //mostly List<Int>, List<String>, Values
    val translations: List<TranslationQuestion>,
    val restricted_to: String? = null,
    val is_active: Boolean = true,
    val required: Boolean = true,
    override val elementtype: String = Constants.ELEMENT_TYPE_QUESTION
) : Element()
