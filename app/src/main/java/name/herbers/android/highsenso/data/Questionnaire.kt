package name.herbers.android.highsenso.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import name.herbers.android.highsenso.database.ElementsConverter

/**
 * Data class for questionnaires.
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "questionnaires_table")
data class Questionnaire(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val name: String,
    @TypeConverters(ElementsConverter::class)
    val questions: List<Element>
)