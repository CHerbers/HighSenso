package name.herbers.android.highsenso.database

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.dispatcher.DefaultDispatcherProvider
import name.herbers.android.highsenso.dispatcher.DispatcherProvider
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
    val database: HighSensoDatabaseDao?,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) {
    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(dispatchers.main() + viewModelJob)

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
            questionnaires = getQuestionnairesFromDatabase() ?: listOf()
            answerSheets = getAnswerSheetsFromDatabase() ?: listOf()
        }
        Timber.i("questions initialized")
    }

    /**
     * Calls a SELECT function from [HighSensoDatabase] to get all questions
     * from the database.
     * @return a [List] of all [Question]s from the database
     * */
    private suspend fun getQuestionnairesFromDatabase(): List<Questionnaire>? {
        return withContext(dispatchers.io()) {
            database?.getAllQuestionnaires()
        }
    }

    /**
     * Calls a SELECT function from [HighSensoDatabase] to get all questions
     * from the database.
     * @return a [List] of all [Question]s from the database
     * */
    private suspend fun getAnswerSheetsFromDatabase(): List<AnswerSheet>? {
        return withContext(dispatchers.io()) {
            database?.getAllPastAnswerSheets()
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [update] within that scope to initiate an database
     * update with the given param.
     * @param questionnaire a [Questionnaire] the database should be updated with
     * */
    fun updateDatabaseQuestionnaire(questionnaire: Questionnaire) {
        uiScope.launch {
            update(questionnaire)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s UPDATE function in order to update a question in the
     * database with a given [Questionnaire].
     * @param questionnaire the [Questionnaire] the database will be updated with
     * */
    private suspend fun update(questionnaire: Questionnaire) {
        withContext(dispatchers.io()) {
            database?.update(questionnaire)
            Timber.i("Updated Question: $questionnaire")
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [update] within that scope to initiate an database
     * update with the given param.
     * @param answerSheet a [AnswerSheet] the database should be updated with
     * */
    fun updateDatabaseAnswerSheet(answerSheet: AnswerSheet) {
        uiScope.launch {
            update(answerSheet)
        }
    }

    /**
     * Calls the [HighSensoDatabaseDao]'s UPDATE function in order to update a question in the
     * database with a given [AnswerSheet].
     * @param answerSheet the [AnswerSheet] the database will be updated with
     * */
    private suspend fun update(answerSheet: AnswerSheet) {
        withContext(dispatchers.io()) {
            database?.update(answerSheet)
            Timber.i("Updated Question: $answerSheet")
        }
    }
}