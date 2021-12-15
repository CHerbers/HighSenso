package name.herbers.android.highsenso.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "questions_table")
data class QuestionB(
    @ColumnInfo
    val questiontype: String,
    @ColumnInfo
    val min: Int?,
    @ColumnInfo
    val max: Int?,
    @ColumnInfo
    val step: Double?,
    @ColumnInfo
    val required: Boolean,
    @PrimaryKey
    val variable: String
)
