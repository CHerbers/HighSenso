package name.herbers.android.highsenso.data

/**
 * Data class for elements.
 * Inherited by [Question] and [Headlines].
 * Used in [Questionnaire]s.
 *
 *@project HighSenso
 *@author Herbers
 */
abstract class Element {
    abstract val position: Int
    abstract val elementtype: String
}
