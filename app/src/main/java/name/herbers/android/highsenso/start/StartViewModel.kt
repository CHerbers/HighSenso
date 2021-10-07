package name.herbers.android.highsenso.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import timber.log.Timber

class StartViewModel(
    val database: QuestionDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private lateinit var questions: List<Question>

    init {
        Timber.i("StartViewModel created!")
        initQuestions()
    }

    fun handleResetQuestions(): Boolean {
        if (this::questions.isInitialized) {
            questions.forEach { question ->
                question.rating = -1
                updateDatabase(question)
            }
        }
        return true
    }


    private fun initQuestions() {
        uiScope.launch {
            questions = getQuestionsFromDatabase()
        }
        Timber.i("questions initialized")
    }

    private suspend fun getQuestionsFromDatabase(): List<Question> {
        return withContext(Dispatchers.IO) {
            val questionsList: List<Question> = database.getAllQuestions()
            questionsList
        }
    }

    //TODO doc comment
    private fun updateDatabase(question: Question) {
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

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }

}