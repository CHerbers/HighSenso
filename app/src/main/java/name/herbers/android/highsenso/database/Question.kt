package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents a question which should be answered by the user
 * @param id  the unique question id for sequencing and distinguishing the questions
 * @param title the name of the question
 * @param question the actual question (content)
 * @param explanation further information on how to interpret/understand the question
 * @param rating is how the question was rated by the user, default value is "-1" which means "unrated"
 * */
@Entity(tableName = "questions_table")
data class Question(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val question: String,
    @ColumnInfo
    val explanation: String,
//    @ColumnInfo
//    val subscale: String,
    @ColumnInfo
    var rating: Int = -1
) {
    override fun toString(): String {
        return "id: '${id}', title: '${title}', question: '${question}', explanation: '${explanation}', rating: '${rating}'"
    }
}