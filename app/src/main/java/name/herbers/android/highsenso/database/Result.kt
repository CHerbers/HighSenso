package name.herbers.android.highsenso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The [Result] data class represents the different results that can be possibly be shown onscreen,
 * depending on the users answering.
 * @param id the unique result id to tell the different results apart
 * @param resultContent the actual result text that is shown onscreen
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Entity (tableName = "results_table")
data class Result(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val resultContent: String
)
