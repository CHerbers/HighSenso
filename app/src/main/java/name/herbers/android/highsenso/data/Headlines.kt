package name.herbers.android.highsenso.data

import name.herbers.android.highsenso.Constants

/**
 * Data class for headlines.
 * Headlines represent the headline/message of a [Questionnaire].
 *
 *@project HighSenso
 *@author Herbers
 */
data class Headlines (
    override val position: Int,
    val translations: List<TranslationHeadline>,
    override val elementtype: String = Constants.ELEMENT_TYPE_HEADLINE
): Element()