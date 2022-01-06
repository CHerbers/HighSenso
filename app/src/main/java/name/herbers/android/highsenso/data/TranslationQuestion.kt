package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class TranslationQuestion(
    val question: String,
    val answers: Any,      //Either String or List<String>
    val locale: String = "de"
)