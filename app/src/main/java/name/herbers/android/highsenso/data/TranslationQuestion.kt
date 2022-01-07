package name.herbers.android.highsenso.data

/**
 * Data class for question translations.
 * This holds the actual question in the specific languages.
 *
 *@project HighSenso
 *@author Herbers
 */
data class TranslationQuestion(
    val question: String,
    val answers: Any,      //Either String or List<String>
    val locale: String = "de"
)