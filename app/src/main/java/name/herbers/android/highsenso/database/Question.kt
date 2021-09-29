package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents the questions the user should answer
 * @param title is the name of the question
 * @param question is the actual question (content)
 * @param explanation further information on how to interpret/understand the question
 * @param valuation is how the question was rated by the user, default value is "-1" which means "not rated"
 * @param number is for sequencing and distinguishing the questions
 * */
@Entity(tableName = "questions_table")
data class Question(
    @PrimaryKey
    val number: Int,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val question: String,
    @ColumnInfo
    val explanation: String,
    @ColumnInfo
    var valuation: Int = -1
) {

    /** Sets the rating the user rated this question with. If given value is illegal,
     * the rating is set to "0" which means "unrated"
     * @param value is the users rating, it should be between 1 and 10 (legal input)
     * */
    fun rate(value: Int) {
        valuation = when {
            value > 4 -> 0
            value < 1 -> 0
            else -> value
        }
    }
}