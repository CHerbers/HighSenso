package name.herbers.android.highsenso.questioning

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.random.Random

/**
 * This is the test Class for [QuestioningViewModel].
 * Every public function is tested in an extra test function.
 * */
@ExperimentalCoroutinesApi
class QuestioningViewModelTest {
    private lateinit var questionDao: QuestionDatabaseDao
    private lateinit var database: QuestionDatabase
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var questioningViewModel: QuestioningViewModel
    private val rating = 4

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
        questioningViewModel = QuestioningViewModel(databaseHandler)
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun getRatingToSetProgressTest() = coroutinesRule.testDispatcher.runBlockingTest {
        assertEquals(rating, questioningViewModel.getRatingToSetProgress())
    }

    @Test
    fun handleNextButtonClickTest() {
        /* just checking if the starting conditions (isFinished Bool and currentQuestion) are as expected */
        assertTrue(
            "Is finished is true but it is just the beginning!",
            questioningViewModel.isFinished.value == false
        )
        assertTrue(
            "The question id is not 1!",
            questioningViewModel.currentQuestion.id == 1
        )

        /* start real testing */
        clickNextButton()   //1st next click -> current question.id = 2
        assertTrue(
            "The question id is not 2!",
            questioningViewModel.currentQuestion.id == 2
        )
        clickNextButton()   //2nd next click -> current question.id = 3
        assertTrue(
            "The question id is not 3!",
            questioningViewModel.currentQuestion.id == 3
        )
        clickNextButton()   //3rd next click -> isFinished should be true
        assertTrue(
            "isFinished is not true, but it should be!",
            questioningViewModel.isFinished.value == true
        )
        clickNextButton()   //4th next click -> should not be possible in real app environment
        //passes test if no Exception is thrown after 4th click
    }

    /**
     * This function represents a nextButton click with a random legal rating (number between 0 and 4).
     * */
    private fun clickNextButton() {
        questioningViewModel.handleNextButtonClick(Random.nextInt(0, 4))
    }

    @Test
    fun handleBackButtonClickTest() = coroutinesRule.testDispatcher.runBlockingTest {
        /* just checking if the starting conditions (isFinished Bool and currentQuestion) are as expected */
        assertTrue(
            "Is finished is true but it is just the beginning!",
            questioningViewModel.isFinished.value == false
        )
        assertTrue(
            "The question id is not 1!",
            questioningViewModel.currentQuestion.id == 1
        )

        /* start real testing */

        /* 1. Click nextButton two times to have something to go back with backButton */
        clickNextButton()   // current question.id = 2
        clickNextButton()   // current question.id = 3

        /* 2. Go back as far as possible */
        clickBackButton() //1st back click -> current question.id = 2
        assertTrue(
            "The question id is not 2!",
            questioningViewModel.currentQuestion.id == 2
        )
        clickBackButton() //2nd back click -> current question.id = 1
        assertTrue(
            "The question id is not 1!",
            questioningViewModel.currentQuestion.id == 1
        )
        clickBackButton() //3rd back click -> _navBackToStartFrag should be true
        assertTrue(
            "navBack is not true, but it should be!",
            questioningViewModel.navBackToStartFrag.value == true
        )
        clickBackButton() //4th back click -> should not be possible in real app environment
        //passes test if no Exception is thrown after 4th click
    }

    /**
     * This function represents a backButton click with a random legal rating (number between 0 and 4).
     * */
    private fun clickBackButton() {
        questioningViewModel.handleBackButtonClick(Random.nextInt(0, 4))
    }

    /**
     * This function creates a bunch of [Question]s and inserts them into the database.
     * */
    private fun fillDatabase() {
        listOf(
            Question(
                1,
                "question01",
                "Is this the first question?",
                "We want to know if this is the first question.",
                rating
            ),
            Question(
                2,
                "question02",
                "Is this a question, too?",
                "We want to know if this is a question.",
                rating
            ),
            Question(
                3,
                "question03",
                "Does this questioning ever stop?",
                "We want to know if there are many questions left to ask.",
                rating
            )
        ).forEach { question ->
            database.questionDatabaseDao.insert(question)
        }
    }
}