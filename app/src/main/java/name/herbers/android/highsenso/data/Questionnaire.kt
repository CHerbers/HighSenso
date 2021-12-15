package name.herbers.android.highsenso.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "questionnaire_table")
data class Questionnaire(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val questions: List<QuestionB>
) {
}