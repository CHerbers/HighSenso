package name.herbers.android.highsenso.result

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import timber.log.Timber

/**
 * [AndroidViewModel] for the [ResultFragment].
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResultViewModel(
    val sharedViewModel: SharedViewModel,
    application: Application
) : AndroidViewModel(application) {

    private val appRes = application.applicationContext.resources
    private val questions = sharedViewModel.databaseHandler.questions

    /* some constants */
    companion object{
        private const val HSP_POSITIVE_LIMIT = 14
    }

    //error messages
    private val errorInvalidInput =
        appRes.getString(R.string.send_dialog_text_edit_error_invalid)
    private val errorTooOld =
        appRes.getString(R.string.send_dialog_text_edit_error_old)
    private val errorTooYoung =
        appRes.getString(R.string.send_dialog_text_edit_error_young)
    private val maxAge = appRes.getInteger(R.integer.max_age)
    private val minAge = appRes.getInteger(R.integer.min_age)
    private val regex = "[0-9]".toRegex()

    /* Observed LiveData */
    //LiveData observed by Fragment to decide if a TextView is shown
    private val _disableHelpTextViews = MutableLiveData<Boolean>()
    val disableHelpTextViews: LiveData<Boolean>
        get() = _disableHelpTextViews

    private val _hasEnthusiasm = MutableLiveData<Boolean>()
    val hasEnthusiasm: LiveData<Boolean>
        get() = _hasEnthusiasm

    private val _isEmotionalVulnerable = MutableLiveData<Boolean>()
    val isEmotionalVulnerable: LiveData<Boolean>
        get() = _isEmotionalVulnerable

    private val _hasSelfDoubt = MutableLiveData<Boolean>()
    val hasSelfDoubt: LiveData<Boolean>
        get() = _hasSelfDoubt

    private val _hasWorldPain = MutableLiveData<Boolean>()
    val hasWorldPain: LiveData<Boolean>
        get() = _hasWorldPain

    private val _hasLingeringEmotions = MutableLiveData<Boolean>()
    val hasLingeringEmotions: LiveData<Boolean>
        get() = _hasLingeringEmotions

    private val _isSuffering = MutableLiveData<Boolean>()
    val isSuffering: LiveData<Boolean>
        get() = _isSuffering

    private val _hasWorkplaceProblem = MutableLiveData<Boolean>()
    val hasWorkplaceProblem: LiveData<Boolean>
        get() = _hasWorkplaceProblem

    //LiveData observed by TextViews
    private val _resultGeneralHspContent = MutableLiveData<String>()
    val resultGeneralHspContent: LiveData<String>
        get() = _resultGeneralHspContent

    private val _sufferingMessageContent = MutableLiveData<String>()
    val sufferingMessageContent: LiveData<String>
        get() = _sufferingMessageContent

    private val _resultPersonalContent = MutableLiveData<String>()
    val resultPersonalContent: LiveData<String>
        get() = _resultPersonalContent

    private val _resultConditionalContent = MutableLiveData<String>()
    val resultConditionalContent: LiveData<String>
        get() = _resultConditionalContent

//    private val _resultWorkplaceContent = MutableLiveData<String>()
//    val resultWorkplaceContent: LiveData<String>
//        get() = _resultWorkplaceContent

    init {
        Timber.i("ResultViewModel created!")
        calculateResult()
    }

    /**
     * This function is calculating, which messages should be shown onscreen, depending on the
     * question rating the user has given.
     * */
    private fun calculateResult() {
        /* Calculate the HSP-Scala-Score */
        var ratingSum = 0
        sharedViewModel.databaseHandler.questions.forEach { question ->
            if (question.itemQuestion)
                if (question.rating)
                    ratingSum++
        }
        Timber.i("Total rating sum: $ratingSum!")

        /* General check if user is a HSP and generating message */
        _resultGeneralHspContent.value = buildGeneralResultString(ratingSum)

        /* checks the influence questions (besides suffering and workplace) and triggers actions to
        * show their specific messages onscreen */
        checkInfluenceQuestions()

        /* Suffering check and message generation */
        if (checkSuffering()) {
            Timber.i("User seems to suffer!")
            _isSuffering.value = true
            _sufferingMessageContent.value =
                if (ratingSum < HSP_POSITIVE_LIMIT) appRes.getString(R.string.detected_suffering_negative_hsp_message)
                else appRes.getString(R.string.detected_suffering_positive_hsp_message)
        }

        /* Workplace problems check and message generation */
        if (checkWorkplaceProblems()) {
            Timber.i("User seems to feel uncomfortable at their workplace!")
            _hasWorkplaceProblem.value = true

        }
        //TODO calculate which result texts from database should be shown onscreen
        //_resultContent = ...
    }

    /**
     * This function checks the rating of the influence questions and sets the specific observed
     * LiveData to true, to trigger that the corresponding TextView is shown onscreen.
     * */
    private fun checkInfluenceQuestions() {
        if (questions.size <= 32) return
        for (i in 27..31) {
            val currentQuestion = questions[i]
            if (currentQuestion.rating) {
                Timber.i(currentQuestion.toString()) //TODO check for question.value instead of the id
                when (currentQuestion.id) {
                    28 -> _hasEnthusiasm.value = true
                    29 -> _isEmotionalVulnerable.value = true
                    30 -> _hasSelfDoubt.value = true
                    31 -> _hasWorldPain.value = true
                    32 -> _hasLingeringEmotions.value = true
                }
            }
        }
    }

    /**
     * This function checks the rating of the questions that are meant to detect if the user is
     * suffering.
     * If one of those three questions was answered positively, the return value is true.
     *
     * @return if the user is suffering.
     * */
    private fun checkSuffering(): Boolean {
        if (questions.size <= 34) return false
        return questions[32].rating || questions[33].rating || questions[34].rating
    }

    /**
     * This function checks the rating of the questions that are meant to detect, if the user feels
     * uncomfortable at their workplace.
     * If one of those two questions was answered positively, the return value is true.
     *
     * @return if the user feels uncomfortable at their workplace.
     * */
    private fun checkWorkplaceProblems(): Boolean {
        if (questions.size <= 36) return false
        return questions[35].rating || questions[36].rating
    }

    fun handleSendResult(age: Int, gender: String) {
        //TODO send the stuff
        //TODO change specific location key after sending
        // preferences.edit().putBoolean(getString(R.string.location_option_work_key), true).apply()
    }

    /**
     * Checks if the given input is a valid age.
     * @param age the users age that is to check
     * @return true if age is valid - false otherwise
     * */
    fun checkSendResultInput(age: Int): Boolean {
        if (regex.containsMatchIn(age.toString()) && age <= maxAge && age >= minAge) {
            Timber.i("$age is a valid age!")
            return true
        }
        Timber.i("$age is an invalid age!")
        return false
    }

    /**
     * Calculates a fitting error message depending on the input String.
     * @param age the users age
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getErrorMessage(age: String): String {
        if (age == "") return ""
        if (!regex.containsMatchIn(age)) return errorInvalidInput
        if (age.toInt() > maxAge) return errorTooOld
        if (age.toInt() < minAge) return errorTooYoung
        return ""
    }

    /**
     * This functions build the string, that shows the result, if the user is a HSP and their
     * HSP-Scala-Score.
     * */
    @SuppressLint("StringFormatMatches")
    fun buildGeneralResultString(rating: Int): String {
        var resultString: String
        val isNegative = rating < HSP_POSITIVE_LIMIT
        var iteration = 1
        val answerSheets = sharedViewModel.answerSheets
        if (answerSheets != null){
            iteration += answerSheets.size
        }

        val probabilityArray = appRes.getStringArray(R.array.general_HSP_result_probability_array)
        val resultPositivity =
            if (rating > HSP_POSITIVE_LIMIT * 1.5 || rating < HSP_POSITIVE_LIMIT * 0.5)
                probabilityArray[0]
            else probabilityArray[1]

        resultString = appRes.getString(
            R.string.general_HSP_part_0_declaration,
            resultPositivity,
            if (isNegative) {
                _disableHelpTextViews.value = true
                appRes.getString(R.string.general_HSP_negative)
            } else ""
        )
        resultString += appRes.getString(
            R.string.general_HSP_part_1_score,
            rating
        )
        resultString +=
            if (isNegative) appRes.getString(R.string.negative_HSP_message)
            else appRes.getString(R.string.positive_HSP_message)
        resultString += appRes.getString(R.string.general_HSP_part_2_iteration, iteration)
        return resultString
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }
}