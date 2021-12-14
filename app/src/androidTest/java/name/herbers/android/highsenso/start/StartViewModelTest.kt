package name.herbers.android.highsenso.start

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import name.herbers.android.highsenso.CoroutineTestRule
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.database.QuestionDatabase
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.random.Random

@ExperimentalCoroutinesApi
class StartViewModelTest {
    private lateinit var questionDao: QuestionDatabaseDao
    private lateinit var database: QuestionDatabase
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var startViewModel: StartViewModel

    @get: Rule
    var coroutinesRule = CoroutineTestRule()
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpDatabase() {
        //create context
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //init in-memory database
        database = Room.inMemoryDatabaseBuilder(context, QuestionDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        //fill database with dummy data
        fillDatabase()

        //init Dao
        questionDao = database.questionDatabaseDao

        databaseHandler = DatabaseHandler(questionDao, coroutinesRule.testDispatcherProvider)
        startViewModel = StartViewModel(databaseHandler)
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun handleResetQuestions() = coroutinesRule.testDispatcher.runBlockingTest {
        //reset questions (fun to test)
        startViewModel.handleResetPassword()

        //test -> load questions from database and check if rating equals -1
        questionDao.getAllQuestions().forEach { question ->
            assertTrue("${question.rating} does not equal -1", question.rating == -1)
        }
    }

    /**
     * This method creates some [Question]s, sets their rating to a random number between 0 and 4
     * and insert them into the database.
     * */
    private fun fillDatabase(){
        listOf(
            Question(
                1,
                "question01",
                "Is this the first question?",
                "We want to know if this is the first question."
            ),
            Question(
                2,
                "question02",
                "Is this a question, too?",
                "We want to know if this is a question."
            ),
            Question(
                3,
                "question03",
                "Does this questioning ever stop?",
                "We want to know if there are many questions left to ask."
            )
        ).forEach { question ->
            question.rating = Random.nextInt(0, 4)
            database.questionDatabaseDao.insert(question)
        }
    }
}
