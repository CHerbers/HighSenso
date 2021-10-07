package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import timber.log.Timber

class QuestioningViewModel(
    val database: QuestionDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    //coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private lateinit var questions: List<Question>
    private lateinit var currentQuestion: Question
    private val defaultRatingProgress =
        application.applicationContext.resources.getInteger(R.integer.default_rating_progress)

    /* current question number / total questions, observed by associated TextView */
    private val _questionCount = MutableLiveData<String>()
    val questionCount: LiveData<String>
        get() = _questionCount

    /* current question title, observed by the associated TextView */
    private val _currentQuestionTitle = MutableLiveData<String>()
    val currentQuestionTitle: LiveData<String>
        get() = _currentQuestionTitle

    /* current question content (actual question), observed by the associated TextView */
    private val _currentQuestionContent = MutableLiveData<String>()
    val currentQuestionContent: LiveData<String>
        get() = _currentQuestionContent

    /* current question explanation, observed by */
    private val _currentQuestionExplanation = MutableLiveData<String>()
    val currentQuestionExplanation: LiveData<String> //TODO show the explanation onscreen (maybe pop-up-like)
        get() = _currentQuestionExplanation

    /* observed by QuestioningFragment, if true: SeekBar is changed to current rating */
    private val _changeSeekBar = MutableLiveData(false)
    val changeSeekBar: LiveData<Boolean>
        get() = _changeSeekBar

    /* observed by QuestioningFragment, if true: navigation to StartFragment */
    private val _navBackToStartFrag = MutableLiveData(false)
    val navBackToStartFrag: LiveData<Boolean>
        get() = _navBackToStartFrag

    /* observed by QuestioningFragment, if true: navigation to ResultFragment */
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    init {
        //load questions from database
        initQuestions()
        Timber.i("QuestioningViewModel created!")
    }

    //TODO doc comment
    private fun initQuestions() {
        uiScope.launch {
            questions = getQuestionsFromDatabase()
            updateLiveData(0)
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
    fun getRatingToSetProgress(): Int {
        if (this::currentQuestion.isInitialized){
            if (currentQuestion.rating >= 0){
                return currentQuestion.rating
            }
        }
        return defaultRatingProgress
    }

    //TODO doc comment
    fun handleBackButtonClick(newRating: Int) {
        updateRatingFromSeekBar(newRating)
        //check if this is the first question
        if (currentQuestion.id == 1) {
            //initiate change to start fragment
            _navBackToStartFrag.value = true
        } else {
            //load previous question
            updateLiveData(currentQuestion.id - 2)
        }
    }

    //TODO doc comment
    fun handleNextButtonClick(newRating: Int) {
        updateRatingFromSeekBar(newRating)
        _navBackToStartFrag.value = false
        //check if this is the last question
        if (currentQuestion.id == questions.size) {
            //initiate change to result fragment
            _isFinished.value = true
            return
        } else {
            //load next question
            updateLiveData(currentQuestion.id)
        }
    }

    //TODO doc comment
    private fun updateLiveData(nextQuestionIndex: Int) {
        currentQuestion = questions[nextQuestionIndex]

        //LiveData
        _questionCount.value = "${currentQuestion.id} / ${questions.size}"
        _currentQuestionTitle.value = currentQuestion.title
        _currentQuestionContent.value = currentQuestion.question
        _currentQuestionExplanation.value = currentQuestion.explanation

        //initiate the change of the progression of SeekBar
        _changeSeekBar.value = true
        _changeSeekBar.value = false
        Timber.i("Current Question: $currentQuestion")
    }

    //TODO doc comment
    private fun updateRatingFromSeekBar(progress: Int) {
        currentQuestion.rating = progress
        updateDatabase(currentQuestion)
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
        viewModelJob.cancel()
        Timber.i("QuestioningViewModel destroyed!")
    }

    //TODO delete methods following this comment

    private fun createQuestionList(): List<Question> {
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