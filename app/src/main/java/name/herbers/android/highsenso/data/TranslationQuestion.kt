package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class TranslationQuestion(
    val locale: String = "de",
    val question: String,
    val answers: List<Any>      //Either List<String> or List<Answer>
)
