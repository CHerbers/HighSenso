package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class Headlines (
    override val position: Int,
    override val elementtype: String,
    val translations: TranslationHeadline
): Element()