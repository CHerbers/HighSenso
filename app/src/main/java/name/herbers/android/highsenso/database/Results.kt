package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "results_table")
data class Results(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val resultContent: String
)
