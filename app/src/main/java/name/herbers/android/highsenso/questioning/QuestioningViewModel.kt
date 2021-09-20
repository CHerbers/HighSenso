package name.herbers.android.highsenso.questioning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.model.Question
import timber.log.Timber

class QuestioningViewModel : ViewModel() {

    private val _isFirstQuestion = MutableLiveData<Boolean>()
    val isFirstQuestion: LiveData<Boolean>
        get() = _isFirstQuestion

    private lateinit var questions: List<Question>
    private lateinit var currentQuestion: Question

    init {
        initQuestionsList()
        _isFirstQuestion.value = true
        Timber.i("QuestioningViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("QuestioningViewModel destroyed!")
    }

    private fun initQuestionsList() {

        //TODO read (?!) questions from XML (or similar) and create Question Objects and save them
        // in the questions List

        Timber.i("questions initialized")
    }

    fun handleBackButtonClick() {
        //TODO check if first question. if no:
        _isFirstQuestion.value = false

        //TODO load last question (if yes)
    }

    //TODO: Calculate what question should be shown and save question order

    //TODO: Save rating if next is pressed
}