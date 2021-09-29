package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import timber.log.Timber

class QuestioningViewModel(
    val database: QuestionDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var questions: List<Question>
    private lateinit var currentQuestion: Question

    private val _currentQuestionTitle = MutableLiveData<String>()
    val currentQuestionTitle: LiveData<String>
        get() = _currentQuestionTitle

    private val _currentQuestionContent = MutableLiveData<String>()
    val currentQuestionContent: LiveData<String>
        get() = _currentQuestionContent

    private val _currentQuestionExplanation = MutableLiveData<String>()
    val currentQuestionExplanation: LiveData<String>
        get() = _currentQuestionExplanation

//    private val _currentValue = MutableLiveData<Int>()
    val currentValue: LiveData<Int>
        get() = getProgress()


    private val _isFirstQuestion = MutableLiveData<Boolean>()
    val isFirstQuestion: LiveData<Boolean>
        get() = _isFirstQuestion

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    private var questionList: List<Question>

    init {
        //create questions
        questionList = createQuestionList()
        initQuestions()
        initQuestionsList()
        currentQuestion = Question(-1,"Title", "Question", "Explanation")
//        currentQuestion = questions[0]
//        updateLiveData()
        _isFirstQuestion.value = true
        _isFinished.value = false
        Timber.i("QuestioningViewModel created!")
    }

    private fun initQuestions() {
        uiScope.launch {
            questionList.forEach { question -> insert(question) }
        }
    }

    private suspend fun insert(question: Question) {
        withContext(Dispatchers.IO){
            database.insert(question)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("QuestioningViewModel destroyed!")
    }

    private fun initQuestionsList() {
        uiScope.launch {
            questions = getQuestionsFromDatabase()
            currentQuestion = questions[0]
            updateLiveData()
        }


        //TODO read (?!) questions from XML (or similar) and create Question Objects and save them
        // in the questions List

        Timber.i("questions initialized")
    }

    private suspend fun getQuestionsFromDatabase(): List<Question> {
        return withContext(Dispatchers.IO){
            val questionsList: List<Question> = database.getAllQuestions()
            questionsList
        }
    }

    private fun getProgress(): MutableLiveData<Int> {
        val progress = MutableLiveData<Int>()
        if (currentQuestion.valuation < 0){
            progress.value = 2
        }else{
            progress.value = currentQuestion.valuation
        }
        return progress
    }

    fun handleBackButtonClick() {
        //check if this is the first question
        _isFirstQuestion.value = currentQuestion.number == 0


        //TODO load last question (if yes)
    }

    fun handleNextButtonClick() {
        if (currentQuestion.number == questions.size - 1) {
            _isFinished.value = true
            return
        } else {
            currentQuestion = questions[currentQuestion.number + 1]
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        _currentQuestionTitle.value = currentQuestion.title
        _currentQuestionContent.value = currentQuestion.question
        _currentQuestionExplanation.value = currentQuestion.explanation
    }

    fun handleSeekBarChanged(progress: Int) {
        currentQuestion.valuation = progress
        updateDatabase(currentQuestion)
    }

    private fun updateDatabase(question: Question) {
        uiScope.launch {
            update(question)
        }
    }

    private suspend fun update(question: Question) {
        withContext(Dispatchers.IO){
            database.update(question)
        }
    }

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
    //TODO: Calculate what question should be shown and save question order

    //TODO: Save rating if next is pressed
}