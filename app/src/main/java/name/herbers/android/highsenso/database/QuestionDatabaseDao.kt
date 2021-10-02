package name.herbers.android.highsenso.database

import androidx.room.*

@Dao
interface QuestionDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Update
    fun update(question: Question)

    //returns all questions, with question with lowest number on list index 0
    @Query("SELECT * FROM questions_table ORDER BY id ASC")
    fun getAllQuestions(): List<Question>

//    @Query("SELECT * FROM questions_table ORDER BY number ASC")
//    fun getAllQuestionsLive(): LiveData<List<Question>>

//    @Query("SELECT * FROM questions_table ORDER BY number DESC LIMIT 1")
//    fun getFirstQuestion(): Question
}