package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents a question which should be answered by the user.
 * @param id  the unique question id for sequencing and distinguishing the questions
 * @param question the actual question (content)
 * @param itemQuestion is true if the question is part of the 27-Items
 * @param rating is how the question was rated by the user
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Entity(tableName = "questions_table")
data class Question(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val question: String,
    @ColumnInfo
    val itemQuestion: Boolean,
    @ColumnInfo
    var rating: Boolean = false
) {
    override fun toString(): String {
        return "id: '${id}', question: '${question}', part of 27-Items: '${itemQuestion}', rating: '${rating}'"
    }
}