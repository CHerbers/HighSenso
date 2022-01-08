package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import name.herbers.android.highsenso.data.Questionnaire

/**
 * This data class is mostly a copy of [Questionnaire].
 * But all [List] elements got replaced with [String]s.
 * These Strings are JSON strings that represent their Questionnaire List equivalents.
 * This was done to easier use the [HighSensoDatabase]
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "questionnaires_table")
data class DatabaseQuestionnaire(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val name: String,
    val questions: String
)
