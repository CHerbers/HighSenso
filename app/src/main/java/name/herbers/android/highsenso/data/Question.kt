package name.herbers.android.highsenso.data

import androidx.room.ColumnInfo

/**
 *
 *@project HighSenso
 *@author Herbers
 */
//@Entity(tableName = "questions_table")
data class Question(
//    @PrimaryKey
    override val position: Int,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val is_active: Boolean,
    @ColumnInfo
    override val elementtype: String,
    @ColumnInfo
    val required: Boolean,
    @ColumnInfo
    val questiontype: String,
    @ColumnInfo
    val label: String,
    @ColumnInfo
    val values: Any,                  //mostly List<Int>, List<String>, Values
    @ColumnInfo
    val restricted_to: String?,
    @ColumnInfo
    val translations: List<TranslationQuestion>
) : Element()
