package name.herbers.android.highsenso.database

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.dispatcher.DefaultDispatcherProvider
import name.herbers.android.highsenso.dispatcher.DispatcherProvider
import org.json.JSONArray
import timber.log.Timber

/**
 * The [DatabaseHandler] is responsible for all communication with [HighSensoDatabase].
 * Every interaction with the database is via this Class. Therefore one DatabaseHandler is
 * provided in every [ViewModel] of this App that has to get or set database data.
 * @property database the [HighSensoDatabaseDao] with the database manipulation logic/queries
 * @property dispatchers a [DispatcherProvider] for the coroutines
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class DatabaseHandler(
    val database: HighSensoDatabaseDao,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) {
    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(dispatchers.main() + viewModelJob)

    private val offlineHelper = OfflineHelper()
    var questionnaires: List<Questionnaire> = listOf()
    var answerSheets: List<AnswerSheet> = listOf()

    init {
        Timber.i("StartViewModel created!")
        loadDataFromDatabase()
    }

    /**
     * Sets the [Questionnaire]s within a launched [CoroutineScope] by calling [getQuestionnairesFromDatabase].
     * Sets the [AnswerSheet]s within a launched [CoroutineScope] by calling [getAnswerSheetsFromDatabase].
     * */
    private fun loadDataFromDatabase() {
        uiScope.launch {
            questionnaires = getRealQuestionnaires(getQuestionnairesFromDatabase())
            answerSheets = getRealAnswerSheets(getAnswerSheetsFromDatabase())
        }
        Timber.i("questions initialized")
    }

    /**
     * This function converts given [DatabaseQuestionnaire]s into [Questionnaire]s
     *
     * @param databaseQuestionnaires the [DatabaseQuestionnaire]s that are to convert
     *
     * @return a [List] of converted [Questionnaire]s
     * */
    private fun getRealQuestionnaires(databaseQuestionnaires: List<DatabaseQuestionnaire>): List<Questionnaire> {
        val returnQuestionnaire = mutableListOf<Questionnaire>()

        databaseQuestionnaires.forEach { databaseQuestionnaire ->
            val questions =
                offlineHelper.getQuestionListFromJsonArray(JSONArray(databaseQuestionnaire.questions))
            returnQuestionnaire.add(
                Questionnaire(
                    databaseQuestionnaire.id,
                    databaseQuestionnaire.name,
                    questions
                )
            )
        }
        return returnQuestionnaire
    }

    /**
     * This function converts given [DatabaseAnswerSheet]s into [AnswerSheet]s
     *
     * @param databaseAnswerSheets the [DatabaseAnswerSheet]s that are to convert
     *
     * @return a [List] of converted [AnswerSheet]s
     * */
    private fun getRealAnswerSheets(databaseAnswerSheets: List<DatabaseAnswerSheet>): List<AnswerSheet> {
        val returnAnswerSheet = mutableListOf<AnswerSheet>()

        databaseAnswerSheets.forEach { databaseAnswerSheet ->
            val answers =
                offlineHelper.getAnswersListFromJSONArray(JSONArray(databaseAnswerSheet.answers))
            val sensorData =
                offlineHelper.getSensorDataListFromJSONArray(JSONArray(databaseAnswerSheet.sensorData))
            returnAnswerSheet.add(
                AnswerSheet(
                    databaseAnswerSheet.id,
                    databaseAnswerSheet.collected_at,
                    answers,
                    sensorData,
                    databaseAnswerSheet.client
                )
            )
        }
        return returnAnswerSheet
    }

    /**
     * Calls a SELECT function from [HighSensoDatabase] to get all questions
     * from the database.
     *
     * @return a [List] of all [DatabaseQuestionnaire]s from the database
     * */
    private suspend fun getQuestionnairesFromDatabase(): List<DatabaseQuestionnaire> {
        return withContext(dispatchers.io()) {
            database.getAllQuestionnaires()
        }
    }

    /**
     * Calls a SELECT function from [HighSensoDatabase] to get all questions
     * from the database.
     *
     * @return a [List] of all [DatabaseAnswerSheet]s from the database
     * */
    private suspend fun getAnswerSheetsFromDatabase(): List<DatabaseAnswerSheet> {
        return withContext(dispatchers.io()) {
            database.getAllPastAnswerSheets()
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [update] within that scope to initiate an database
     * update with the given param.
     *
     * @param questionnaire a [DatabaseQuestionnaire] the database should be updated with
     * */
    fun updateDatabaseQuestionnaire(questionnaire: DatabaseQuestionnaire) {
        uiScope.launch {
            update(questionnaire)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s UPDATE function in order to update a Questionnaire in the
     * database with a given [DatabaseQuestionnaire].
     *
     * @param questionnaire the [DatabaseQuestionnaire] the database will be updated with
     * */
    private suspend fun update(questionnaire: DatabaseQuestionnaire) {
        withContext(dispatchers.io()) {
            database.update(questionnaire)
            Timber.i("Updated Questionnaire: $questionnaire")
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [insert] within that scope to initiate the insertion of
     * a given [DatabaseQuestionnaire].
     *
     * @param questionnaire the [DatabaseQuestionnaire] that is to be inserted into the [HighSensoDatabase]
     * */
    fun insertQuestionnaire(questionnaire: DatabaseQuestionnaire) {
        uiScope.launch {
            insert(questionnaire)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s INSERT function in order to insert a given [DatabaseQuestionnaire]
     * into the [HighSensoDatabase].
     *
     * @param questionnaire the [DatabaseQuestionnaire] that is to be inserted into the [HighSensoDatabase]
     * */
    private suspend fun insert(questionnaire: DatabaseQuestionnaire) {
        withContext(dispatchers.io()) {
            database.insert(questionnaire)
            Timber.i("Inserted Questionnaire: $questionnaire")
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [update] within that scope to initiate an database
     * update with the given param.
     * @param answerSheet a [DatabaseAnswerSheet] the database should be updated with
     * */
    fun updateDatabaseAnswerSheet(answerSheet: DatabaseAnswerSheet) {
        uiScope.launch {
            update(answerSheet)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s UPDATE function in order to update a AnswerSheet in the
     * database with a given [DatabaseAnswerSheet].
     * @param answerSheet the [DatabaseAnswerSheet] the database will be updated with
     * */
    private suspend fun update(answerSheet: DatabaseAnswerSheet) {
        withContext(dispatchers.io()) {
            database.update(answerSheet)
            Timber.i("Updated AnswerSheet: $answerSheet")
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [insert] within that scope to initiate the insertion of
     * a given [DatabaseAnswerSheet].
     *
     * @param answerSheet the [DatabaseAnswerSheet] that is to be inserted into the [HighSensoDatabase]
     * */
    fun insertAnswerSheet(answerSheet: DatabaseAnswerSheet) {
        uiScope.launch {
            insert(answerSheet)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s INSERT function in order to insert a given [DatabaseAnswerSheet]
     * into the [HighSensoDatabase].
     *
     * @param answerSheet the [DatabaseAnswerSheet] that is to be inserted into the [HighSensoDatabase]
     * */
    private suspend fun insert(answerSheet: DatabaseAnswerSheet) {
        withContext(dispatchers.io()) {
            database.insert(answerSheet)
            Timber.i("Inserted AnswerSheet: $answerSheet")
        }
    }
}