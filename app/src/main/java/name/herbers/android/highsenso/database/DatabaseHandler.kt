package name.herbers.android.highsenso.database

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.herbers.android.highsenso.dispatcher.DefaultDispatcherProvider
import name.herbers.android.highsenso.dispatcher.DispatcherProvider
import timber.log.Timber

/**
 * The [DatabaseHandler] is responsible for all communication with [QuestionDatabaseDao].
 * Every interaction with the database is via this Class. Therefore one DatabaseHandler is
 * provided in every [ViewModel] of this App that has to get or set database data.
 * @property database the [QuestionDatabaseDao] with the database manipulation logic/queries
 * @property dispatchers a [DispatcherProvider] for the coroutines
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class DatabaseHandler(
    val database: QuestionDatabaseDao,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) {
    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(dispatchers.main() + viewModelJob)

    var questions:List<Question> = listOf()

    init {
        Timber.i("StartViewModel created!")
        initQuestions()
    }

    /**
     * Sets the [questions] within a launched [CoroutineScope] by calling [getQuestionsFromDatabase].
     * */
    private fun initQuestions() {
        uiScope.launch {
            questions = getQuestionsFromDatabase()
        }
        Timber.i("questions initialized")
    }

    /**
     * Calls a SELECT function from [QuestionDatabaseDao] to get all questions
     * from the database.
     * @return a [List] of all [Question]s from the database
     * */
    private suspend fun getQuestionsFromDatabase(): List<Question> {
        return withContext(dispatchers.io()) {
            val questionsList: List<Question> = database.getAllQuestions()
            questionsList
        }
    }

    /**
     * Launches a [CoroutineScope] and calls [update] within that scope to initiate an database
     * update with the given param.
     * @param question a [Question] the database should be updated with
     * */
    fun updateDatabase(question: Question) {
        uiScope.launch {
            update(question)
        }
    }

    /**
     * Calls the [QuestionDatabaseDao]'s UPDATE function in order to update a question in the
     * database with a given [Question].
     * @param question the [Question] the database will be updated with
     * */
    private suspend fun update(question: Question) {
        withContext(dispatchers.io()) {
            database.update(question)
            Timber.i("Updated Question: $question")
        }
    }
}