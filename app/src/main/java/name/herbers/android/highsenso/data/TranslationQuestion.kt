package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class TranslationQuestion(
    val question: String,
    val answers: List<Any>,      //Either List<String> or List<Answer>
    val locale: String = "de"
)
