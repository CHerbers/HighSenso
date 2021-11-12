package name.herbers.android.highsenso.questioning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.Question
import name.herbers.android.highsenso.result.ResultFragment
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**
 * [ViewModel] for the [QuestioningFragment].
 * Handles all non-UI tasks for the QuestioningFragment.
 *
 * This includes handlers for Button clicks and SeekBar changes. Updating from LiveData that is
 * shown onscreen and calling of the [DatabaseHandler] for database manipulations.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class QuestioningViewModel(
    private val databaseHandler: DatabaseHandler,
    startingQuestionPos: Int = 0
) : ViewModel() {

    private var questions: List<Question> = databaseHandler.questions
    lateinit var currentQuestion: Question
    private val defaultRatingProgress = 2

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
        updateLiveData(startingQuestionPos)
        Timber.i("QuestioningViewModel created!")
    }

    /**
     * This method checks, if the [currentQuestion] is already initialized and returns its rating
     * value if so and a default progress rating if not.
     * @return the [currentQuestion]s rating if initialized and greater or even '0',
     * or else the [defaultRatingProgress]
     * */
    fun getRatingToSetProgress(): Int {
        if (this::currentQuestion.isInitialized) {
            if (currentQuestion.rating >= 0) {
                return currentQuestion.rating
            }
        }
        return defaultRatingProgress
    }

    /**
     * This method calls a function to update the rating and decides whether the current question
     * gets changed to the next one or a fragment change to the [StartFragment] is initiated
     * @param newRating the new rating the update function is called with
     * */
    fun handleBackButtonClick(newRating: Int) {
        updateRatingFromSeekBar(newRating)
        //check if this is the first question (also triggers if id is smh. smaller than 1)
        if (currentQuestion.id <= 1) {
            //initiate change to start fragment
            _navBackToStartFrag.value = true
        } else {
            //load previous question
            updateLiveData(currentQuestion.id - 2)
        }
    }

    /**
     * This method calls a function to update the rating and decides whether the current question
     * gets changed to the previous one or a fragment change to the [ResultFragment] is initiated
     * @param newRating the new rating the update function is called with
     * */
    fun handleNextButtonClick(newRating: Int) {
        updateRatingFromSeekBar(newRating)
        _navBackToStartFrag.value = false
        //check if this is the last question
        if (currentQuestion.id == questions.size) {
            //initiate change to result fragment
            _isFinished.value = true
        } else {
            //load next question
            updateLiveData(currentQuestion.id)
        }
    }

    /**
     * This method changes the [currentQuestion] and updates all question-related [LiveData] to
     * the corresponding data of the current question. This LiveData is observed by UI elements.
     * A change of the seekBar is triggered to update it on the changed LiveData.
     * @param nextQuestionIndex the index of the question that becomes [currentQuestion]
     * */
    private fun updateLiveData(nextQuestionIndex: Int) {
        //catches if fun is called with out of bound index (should never happen)
        if (nextQuestionIndex >= questions.size || nextQuestionIndex < 0) {
            Timber.e(
                "updateLiveData is called with invalid index! " +
                        "Index will be changed index of last question!"
            )
            updateLiveData(questions.size - 1)
            return
        }

        currentQuestion = questions[nextQuestionIndex]

        //LiveData
        _questionCount.value = "${currentQuestion.id} / ${questions.size}"
        _currentQuestionTitle.value = currentQuestion.title
        _currentQuestionContent.value = currentQuestion.question

        //trigger change of the progression of SeekBar
        _changeSeekBar.value = true
        _changeSeekBar.value = false
        Timber.i("Current Question: $currentQuestion")
    }

    /**
     * This method updates the rating of the [currentQuestion] and updates the corresponding
     * database entry.
     * @param progress the new rating
     * */
    private fun updateRatingFromSeekBar(progress: Int) {
        currentQuestion.rating = progress
        databaseHandler.updateDatabase(currentQuestion)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("QuestioningViewModel destroyed!")
    }
}