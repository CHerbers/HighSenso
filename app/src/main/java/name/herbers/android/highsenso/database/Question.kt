package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents the questions the user should answer
 * @param title is the name of the question
 * @param question is the actual question (content)
 * @param explanation further information on how to interpret/understand the question
 * @param rating is how the question was rated by the user, default value is "-1" which means "not rated"
 * @param id is for sequencing and distinguishing the questions
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
    @ColumnInfo
    var rating: Int = -1
) {
    override fun toString(): String {
        return "id: '${id}', title: '${title}', question: '${question}', explanation: '${explanation}', rating: '${rating}'"
    }
}