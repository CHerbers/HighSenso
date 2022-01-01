package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class Question(
    override val position: Int,
    val name: String,
    val is_active: Boolean,
    override val elementtype: String,
    val required: Boolean,
    val questiontype: String,
    val label: String,
    val values: Any,                  //mostly List<Int>, List<String>, Values
    val restricted_to: String?,
    val translations: List<TranslationQuestion>
) : Element()
