package name.herbers.android.highsenso.data

/**
 * Data class for headline translations.
 * This holds the actual headline in the specific languages.
 *
 *@project HighSenso
 *@author Herbers
 */
data class TranslationHeadline(
    val headline: String,
    val locale: String = "de"
)
