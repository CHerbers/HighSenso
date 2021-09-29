package name.herbers.android.highsenso

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class QuestionDatabaseTest {

    private lateinit var questionDao: QuestionDatabaseDao
    private lateinit var database: QuestionDatabase

    @Before
    fun createDatabase() {
        //create context
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //init in-memory database
        database = Room.inMemoryDatabaseBuilder(context, QuestionDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        //init Dao
        questionDao = database.questionDatabaseDao
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    /**
     * This test tests:
     * a) the insertion of new [Question]s into the database with [QuestionDatabaseDao.insert]
     * b) the selection of all [Question]s from the database with [QuestionDatabaseDao.getAllQuestions]
     * c) the update of a [Question] in the database with [QuestionDatabaseDao.update]
     * */
    @Test
    @kotlin.jvm.Throws(Exception::class)
    fun manipulateDatabase() {
        //create questions
        val questionList = createQuestionList()

        //test insert() function and getAllQuestions()
        questionList.forEach { question -> questionDao.insert(question) }
        var loadedQuestionList: List<Question> = questionDao.getAllQuestions()
        assertEquals("question01", loadedQuestionList[0].title)

        //test update() function
        val questionNew = Question(
            0,
            "question04",
            "Is this question now updated?",
            "We want to know if the update function works."
        )
        questionDao.update(questionNew)
        loadedQuestionList = questionDao.getAllQuestions()
        assertEquals("question04", loadedQuestionList[0].title)
    }

    @Test
    fun updateNonExistingQuestion(){
        val question = Question(4,
            "notAQuestion",
            "I am no question!",
            "This is really not a question."
        )
        questionDao.update(question)
        assertEquals(0, questionDao.getAllQuestions().size)
    }

    /**
     * Creates a [List] of defined [Question]s
     * @return a [List] of [Question]s
     * */
    private fun createQuestionList(): List<Question>{
        val question0 = Question(
            0,
            "question01",
            "Is this the first question?",
            "We want to know if this is the first question."
        )
        val question1 = Question(
            1,
            "question02",
            "Is this a question, too?",
            "We want to know if this is a question."
        )
        val question2 = Question(
            2,
            "question03",
            "Does this questioning ever stop?",
            "We want to know if there are many questions left to ask."
        )
        return listOf(question0, question1, question2)
    }
}