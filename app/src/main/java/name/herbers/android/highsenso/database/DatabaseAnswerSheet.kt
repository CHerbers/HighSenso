package name.herbers.android.highsenso.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Client

/**
 * This data class is mostly a copy of [AnswerSheet].
 * But all [List] elements got replaced with [String]s.
 * These Strings are JSON strings that represent their AnswerSheet List equivalents.
 * This was done to easier use the [HighSensoDatabase]
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "answer_sheets_table")
data class DatabaseAnswerSheet(
    val id: Int,        //questionnaire id
    @PrimaryKey
    val collected_at: Long,
    val answers: String,
    val sensorData: String,
    @TypeConverters(ClientConverter::class)
    val client: Client,
    val locale: String = "de"
)
