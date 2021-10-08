package name.herbers.android.highsenso.database

import kotlinx.coroutines.*
import timber.log.Timber

class DatabaseHandler(
    var database: QuestionDatabaseDao
) {

    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var questions:List<Question> = listOf()

    init {
        Timber.i("StartViewModel created!")
        initQuestions()
    }

    //TODO doc comment
    private fun initQuestions() {
        uiScope.launch {
            questions = getQuestionsFromDatabase()
        }
        Timber.i("questions initialized")
    }

    //TODO doc comment
    private suspend fun getQuestionsFromDatabase(): List<Question> {
        return withContext(Dispatchers.IO) {
            val questionsList: List<Question> = database.getAllQuestions()
            questionsList
        }
    }

    //TODO doc comment
    fun updateDatabase(question: Question) {
        uiScope.launch {
            update(question)
        }
    }

    //TODO doc comment
    private suspend fun update(question: Question) {
        withContext(Dispatchers.IO) {
            database.update(question)
            Timber.i("Updated Question: $question")
        }
    }
}