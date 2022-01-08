package name.herbers.android.highsenso.database

import androidx.room.*

/**
 * Data access interface* for [HighSensoDatabase].
 * Holds insert, update and query functions for database manipulation.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Dao
interface HighSensoDatabaseDao {

    /**
     * Inserts a [DatabaseQuestionnaire] into the [HighSensoDatabase]. If a Questionnaire with the specific
     * *id* (*PrimaryKey*) already exists, the old Questionnaire will be replaced with the new one.
     *
     * @param questionnaire the [DatabaseQuestionnaire] that shall be inserted into the [HighSensoDatabase]
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaire: DatabaseQuestionnaire)

    /**
     * Inserts an [DatabaseAnswerSheet] into the [HighSensoDatabase]. If a AnswerSheet with the specific
     * *id* (*PrimaryKey*) already exists, the old AnswerSheet will be replaced with the new one.
     *
     * @param answerSheet the [DatabaseAnswerSheet] that shall be inserted into the [HighSensoDatabase]
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answerSheet: DatabaseAnswerSheet)

    /**
     * Updates a [DatabaseQuestionnaire] in [HighSensoDatabase].
     *
     * @param questionnaire the [DatabaseQuestionnaire] that is to be updated
     * */
    @Update
    fun update(questionnaire: DatabaseQuestionnaire)

    /**
     * Updates a [DatabaseAnswerSheet] in [HighSensoDatabase].
     *
     * @param answerSheet the [DatabaseAnswerSheet] that is to be updated
     * */
    @Update
    fun update(answerSheet: DatabaseAnswerSheet)

    /**
     * Selects every [DatabaseQuestionnaire] in the [HighSensoDatabase].
     *
     * @return a [List] of all [DatabaseQuestionnaire]s from the [HighSensoDatabase]
     * */
    @Query("SELECT * FROM questionnaires_table")
    fun getAllQuestionnaires(): List<DatabaseQuestionnaire>

    /**
     * Selects every [DatabaseAnswerSheet] in the [HighSensoDatabase].
     *
     * @return a [List] of all [DatabaseAnswerSheet]s from the [HighSensoDatabase]
     * */
    @Query("SELECT * FROM answer_sheets_table")
    fun getAllPastAnswerSheets(): List<DatabaseAnswerSheet>

}