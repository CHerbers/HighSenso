package name.herbers.android.highsenso.model

/** This class represents the questions asked the user
 * @param name is the name of the question
 * @param question is the actual question (content)
 * @param rate is how the question was rated by the user, default value is "0" which means "unrated"
 * */
class Question(
    private val name: String,
    private val question: String,
    private var rate: Int = 0
) {

    private val isRated: Boolean
        get() = rate != 0

    /** This method sets the rating the user rated this question with. If given value is illegal,
     * the rating is set to "0" which means "unrated"
     * @param value is the users rating, it should be between 1 and 10 (legal input)
     * */
    fun rate(value: Int) {
        rate = when {
            value > 10 -> 0
            value < 1 -> 0
            else -> value
        }
    }
}