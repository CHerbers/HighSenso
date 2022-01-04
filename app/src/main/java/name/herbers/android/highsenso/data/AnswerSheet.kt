package name.herbers.android.highsenso.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import name.herbers.android.highsenso.database.AnswerConverter
import name.herbers.android.highsenso.database.ClientConverter
import name.herbers.android.highsenso.database.SensorDataConverter

/**
 *
 *@project HighSenso
 *@author Herbers
 */
@Entity(tableName = "answer_sheets_table")
data class AnswerSheet(
    @PrimaryKey
    val id: Int,
    val collected_at: Long,
    @ColumnInfo
    val locale: String = "de",
    @ColumnInfo
    @TypeConverters(AnswerConverter::class)
    val answers: List<Answer>,
    @TypeConverters(SensorDataConverter::class)
    val sensorData: List<SensorData>,
    @TypeConverters(ClientConverter::class)
    val client: Client
)
