package name.herbers.android.highsenso.database

import androidx.room.*

/**
 * *Data access interface* for [QuestionDatabase].
 * Holds insert, update and query functions.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Dao
interface QuestionDatabaseDao {

    /**
     * Inserts a [Question] in [QuestionDatabase]. If a Question with the specific *id* (*PrimaryKey*)
     * already exists, the old Question will be replaced with the new one.
     *
     * This function does only exist for different *test cases*! Within the app this function will
     * never be used!
     * @param question the [Question] that shall be inserted into the [QuestionDatabase]
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    /**
     * Updates a [Question] in [QuestionDatabase].
     * @param question the [Question] that is to be updated
     * */
    @Update
    fun update(question: Question)

    /**
     * Selects every question in the database with ascendant question ids.
     * @return a [List] of all [Question]s from the [QuestionDatabase]
     * */
    //returns all questions, with question with lowest number on list index 0
    @Query("SELECT * FROM questions_table ORDER BY id ASC")
    fun getAllQuestions(): List<Question>

//    @Query("SELECT * FROM questions_table ORDER BY number DESC LIMIT 1")
//    fun getFirstQuestion(): Question
}