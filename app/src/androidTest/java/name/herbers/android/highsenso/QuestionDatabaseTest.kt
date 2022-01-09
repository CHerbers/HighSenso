package name.herbers.android.highsenso

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.database.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class QuestionDatabaseTest {

    private lateinit var databaseDao: HighSensoDatabaseDao
    private lateinit var database: HighSensoDatabase
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var questionnaires: List<Questionnaire>
    private val jsonParser = HighSensoJsonParser()
    private val gson = Gson()

    @Before
    fun createDatabase() {
        //create context
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //init in-memory database
        database = Room.inMemoryDatabaseBuilder(context, HighSensoDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        //init Dao
        databaseDao = database.highSensoDatabaseDao
        databaseHandler = DatabaseHandler(databaseDao)
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    /**
     * This test tests:
     * a) the insertion of new [Questionnaire]s into the database with [HighSensoDatabaseDao.insert]
     * b) the selection of all [Questionnaire]s from the database with [HighSensoDatabaseDao.getAllQuestionnaires]
     * */
    @Test
    @kotlin.jvm.Throws(Exception::class)
    fun manipulateDatabase() {
        /* test insert() function and getAllQuestions() */
        //create questionnaires
        questionnaires =
            jsonParser.getQuestionnaires(InstrumentationRegistry.getInstrumentation().targetContext)
        //insert
        questionnaires.forEach { questionnaire ->
            databaseDao.insert(
                DatabaseQuestionnaire(
                    questionnaire.id,
                    questionnaire.name,
                    gson.toJson(questionnaire.questions)
                )
            )
        }

        //select all
        val loadedQuestionnaires =
            databaseHandler.getRealQuestionnaires(databaseDao.getAllQuestionnaires())
        //test
        loadedQuestionnaires.forEach { loadedQuestionnaire ->
            val loadedQuestionnaireQuestions = loadedQuestionnaire.questions
            val questionnaireQuestions = questionnaires[loadedQuestionnaire.id - 1].questions
            assertEquals(
                questionnaireQuestions[0].elementtype,
                loadedQuestionnaireQuestions[0].elementtype
            )
            assertEquals(
                (questionnaireQuestions[1] as Question).name,
                (loadedQuestionnaireQuestions[1] as Question).name
            )
            assertEquals(
                (questionnaireQuestions[2] as Question).values,
                (loadedQuestionnaireQuestions[2] as Question).values
            )
            assertEquals(
                (questionnaireQuestions[3] as Question).translations[0].question,
                (loadedQuestionnaireQuestions[3] as Question).translations[0].question
            )
        }
    }
}