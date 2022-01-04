package name.herbers.android.highsenso.database

import androidx.room.*
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Questionnaire

/**
 * *Data access interface* for [HighSensoDatabase].
 * Holds insert, update and query functions.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Dao
interface HighSensoDatabaseDao {

    /**
     * Inserts a [Questionnaire] in [HighSensoDatabase]. If a Questionnaire with the specific *id*
     * (*PrimaryKey*) already exists, the old Question will be replaced with the new one.
     *
     * This function does only exist for different *test cases*! Within the app this function will
     * never be used!
     * @param questionnaire the [Questionnaire] that shall be inserted into the [HighSensoDatabase]
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaire: Questionnaire)

    /**
     * Updates a [Questionnaire] in [HighSensoDatabase].
     * @param questionnaire the [Questionnaire] that is to be updated
     * */
    @Update
    fun update(questionnaire: Questionnaire)

    /**
     * Updates a [AnswerSheet] in [HighSensoDatabase].
     * @param answerSheet the [AnswerSheet] that is to be updated
     * */
    @Update
    fun update(answerSheet: AnswerSheet)

    /**
     * Selects every [Questionnaire] in the [HighSensoDatabase].
     * @return a [List] of all [Questionnaire]s from the [HighSensoDatabase]
     * */
    @Query("SELECT * FROM questionnaires_table")
    fun getAllQuestionnaires(): List<Questionnaire>

    /**
     * Selects every [AnswerSheet] in the [HighSensoDatabase].
     * @return a [List] of all [AnswerSheet]s from the [HighSensoDatabase]
     * */
    @Query("SELECT * FROM answer_sheets_table")
    fun getAllPastAnswerSheets(): List<AnswerSheet>

}