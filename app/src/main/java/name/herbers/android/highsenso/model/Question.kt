package name.herbers.android.highsenso.model

/** Represents the questions the user should answer
 * @param name is the name of the question
 * @param question is the actual question (content)
 * @param explanation further information on how to interpret/understand the question
 * @param valuation is how the question was rated by the user, default value is "0" which means "unrated"
 * */
class Question(
    private val name: String,
    private val question: String,
    private val explanation: String,
    private var valuation: Int = 0
) {

    private val isRated: Boolean
        get() = valuation != 0

    /** Sets the rating the user rated this question with. If given value is illegal,
     * the rating is set to "0" which means "unrated"
     * @param value is the users rating, it should be between 1 and 10 (legal input)
     * */
    fun rate(value: Int) {
        valuation = when {
            value > 10 -> 0
            value < 1 -> 0
            else -> value
        }
    }
}