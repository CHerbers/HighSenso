package name.herbers.android.highsenso.questioning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.data.Answer
import name.herbers.android.highsenso.data.Headlines
import name.herbers.android.highsenso.data.Question
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.result.ResultFragment
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber
import java.util.*

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
    private val sharedViewModel: SharedViewModel,
    startingQuestionPos: Int = 0,
    questionnairePos: Int = 0
) : ViewModel() {

    private var questionnaires = sharedViewModel.questionnaires
    private var questionsList: List<List<Question>> =
        getQuestionsList()
    private lateinit var currentQuestion: Question
    private var currentPosQuestionnaire = questionnairePos
    private var currentPosQuestion = startingQuestionPos

    private val questionnaireIndexOffset = (questionnaires?.size ?: 0) - questionsList.size

    /* current question number / total questions, observed by associated TextView */
    private val _questionCount = MutableLiveData<String>()
    val questionCount: LiveData<String>
        get() = _questionCount

    private val _headline = MutableLiveData<String>()
    val headline: LiveData<String>
        get() = _headline

    /* current question content (actual question), observed by the associated TextView */
    private val _currentQuestionContent = MutableLiveData<String>()
    val currentQuestionContent: LiveData<String>
        get() = _currentQuestionContent

    /* current question rating, observed to change checked status of radioButtons */
    private val _questionRating = MutableLiveData("")
    val questionRating: LiveData<String>
        get() = _questionRating

    /* observed by QuestioningFragment, if true: navigation to StartFragment */
    private val _navBackToStartFrag = MutableLiveData(false)
    val navBackToStartFrag: LiveData<Boolean>
        get() = _navBackToStartFrag

    /* observed by QuestioningFragment, if true: navigation to ResultFragment */
    private val _navToResultFrag = MutableLiveData(false)
    val navToResultFrag: LiveData<Boolean>
        get() = _navToResultFrag

    /* observed by QuestioningFragment, if true: a dialog is shown  */
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    /* observed by QuestioningFragment, if true: navigation to ResultFragment */
    private val _isLastQuestion = MutableLiveData(false)
    val isLastQuestion: LiveData<Boolean>
        get() = _isLastQuestion

    init {
        updateLiveData()
        Timber.i("QuestioningViewModel created!")
    }

    /**
     * This method calls a function to update the rating and decides whether the current question
     * gets changed to the next one or a fragment change to the [StartFragment] is initiated
     * */
    fun handleBackButtonClick() {
        _isLastQuestion.value = false
        when (currentPosQuestionnaire <= 0) {
            true -> {
                if (currentPosQuestion <= 0) {
                    _navBackToStartFrag.value = true
                } else {
                    currentPosQuestion--
                    sharedViewModel.currentQuestionnaireName =
                        questionnaires?.get(currentPosQuestionnaire + questionnaireIndexOffset)?.name
                            ?: ""
                    updateLiveData()
                }
            }
            false -> {
                if (currentPosQuestion <= 0) {
                    currentPosQuestionnaire--
                    currentPosQuestion = questionsList[currentPosQuestionnaire].size - 1
                    updateLiveData()
                } else {
                    currentPosQuestion--
                    updateLiveData()
                }
            }
        }
    }

    /**
     * This method calls a function to update the rating and decides whether the current question
     * gets changed to the previous one or a fragment change to the [ResultFragment] is initiated
     * @param newRating the new rating the update function is called with
     * */
    fun handleNextButtonClick(newRating: Int) {
        val map =
            sharedViewModel.currentAnswers[questionnaires?.get(currentPosQuestionnaire + questionnaireIndexOffset)?.name]
        if (map != null) {
            map[currentQuestion.label] =
                Answer(newRating.toString(), currentQuestion.label, Date().time)
        }

        _navBackToStartFrag.value = false

        if (questionsList.size == currentPosQuestionnaire + 1) {
            if (questionsList[currentPosQuestionnaire].size == currentPosQuestion + 1) {
                _isFinished.value = true
            } else {
                _isLastQuestion.value =
                    questionsList[currentPosQuestionnaire].size == currentPosQuestion + 2
            }
            currentPosQuestion++
        } else {
            if (questionsList[currentPosQuestionnaire].size == currentPosQuestion + 1) {
                currentPosQuestionnaire++
                sharedViewModel.currentQuestionnaireName =
                    questionnaires?.get(currentPosQuestionnaire + questionnaireIndexOffset)?.name
                        ?: ""
                currentPosQuestion = 0
            } else {
                currentPosQuestion++
            }
        }

        updateLiveData()
    }

    /**
     * This method changes the [currentQuestion] and updates all question-related [LiveData] to
     * the corresponding data of the current question. This LiveData is observed by UI elements.
     * A change of the seekBar is triggered to update it on the changed LiveData.
     * */
    private fun updateLiveData() {
        //catches if fun is called with out of bound index (should never happen)
        if (questionsList.size <= currentPosQuestionnaire || questionsList[currentPosQuestionnaire].size <= currentPosQuestion) {
            Timber.e(
                "Question index is somehow invalid! " +
                        "Index will be changed to index of last question!"
            )
            currentPosQuestionnaire = questionsList.size - 1
            currentPosQuestion = questionsList[currentPosQuestionnaire].size - 1
            return
        }
        currentQuestion = questionsList[currentPosQuestionnaire][currentPosQuestion]

        //LiveData
        _questionCount.value =
            "${currentPosQuestion + 1 + numberOfQuestions(true)} / ${numberOfQuestions(false)}"
        _currentQuestionContent.value = currentQuestion.translations[0].question

        val sizeDiff = (questionnaires?.size ?: 0) - questionsList.size
        val element =
            sharedViewModel.questionnaires?.get(currentPosQuestionnaire + sizeDiff)?.questions?.get(
                0
            )
        if (element != null && element.elementtype == Constants.ELEMENT_TYPE_HEADLINE) {
            _headline.value = (element as Headlines).translations[0].headline
        }

        _questionRating.value = ""
        val map =
            sharedViewModel.currentAnswers[questionnaires?.get(currentPosQuestionnaire + sizeDiff)?.name]
        if (map != null) {
            val answer = map[currentQuestion.label]
            if (answer != null) {
                _questionRating.value = answer.value
            }
        }
    }

    /**
     * Calculates the total number of [Question] in [questionsList].
     * Or the number of Questions up to the current Question if [toExclude] is true.
     *
     * @param toExclude excludes all upcoming [Question]s
     *
     * @return total number of [Question], or total number of Questions up to the current Question
     * if [toExclude] is true
     * */
    private fun numberOfQuestions(toExclude: Boolean): Int {
        var sum = 0
        for (i in questionsList.indices) {
            if (toExclude && i >= currentPosQuestionnaire) {
                return sum
            }
            sum += questionsList[i].size
        }
        return sum
    }

    /**
     * Change observed [navToResultFrag] to true.
     * This triggers the navigation to the [ResultFragment].
     * */
    fun navigateToResultFragment() {
        _navToResultFrag.value = true
        _navToResultFrag.value = false
    }

    /**
     * Creates a [List] of a List of [Question]s.
     * Questions of the inner List are from the same [Questionnaire].
     *
     * @return a [List] of a [List] of all [Question]s excluding the baseline [Questionnaire]
     * */
    private fun getQuestionsList(): List<List<Question>> {
        val questionsOuterList =
            mutableListOf<MutableList<Question>>()
        questionnaires?.forEach { questionnaire ->
            if (questionnaire.name != Constants.BASELINE_QUESTIONNAIRE) {
                val questionsInnerList =
                    mutableListOf<Question>()
                questionnaire.questions.forEach { element ->
                    if (element.elementtype == Constants.ELEMENT_TYPE_QUESTION)
                        if ((element as Question).label != Constants.LOCATION_QUESTION_LABEL)
                            questionsInnerList.add(element)
                }
                questionsOuterList.add(questionsInnerList)
            }
        }
        return questionsOuterList
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("QuestioningViewModel destroyed!")
    }
}