package name.herbers.android.highsenso.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import name.herbers.android.highsenso.database.AnswerConverter
import name.herbers.android.highsenso.database.ClientConverter
import name.herbers.android.highsenso.database.SensorDataConverter
import name.herbers.android.highsenso.database.HighSensoDatabase

/**
 * Data class for answer sheets.
 * Can be stored in the [HighSensoDatabase].
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "answer_sheets_table")
data class AnswerSheet(
    val id: Int,        //questionnaire id
    @PrimaryKey
    val collected_at: Long,
    @TypeConverters(AnswerConverter::class)
    val answers: List<Answer>,
    @TypeConverters(SensorDataConverter::class)
    val sensorData: List<SensorData>?,
    @TypeConverters(ClientConverter::class)
    val client: Client,
    val locale: String = "de"
)
