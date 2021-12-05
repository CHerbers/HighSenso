package name.herbers.android.highsenso.result

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * [AndroidViewModel] for the [ResultFragment].
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResultViewModel(
    val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

    /* some constants */
    private val appRes = application.applicationContext.resources
    private val questions = databaseHandler.questions
    private val limitPositive = appRes.getInteger(R.integer.hsp_positive_limit)
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
    //LiveData observed by Fragment
    private val _disableHelpTextViews = MutableLiveData<Boolean>()
    val disableHelpTextViews: LiveData<Boolean>
        get() = _disableHelpTextViews

    private val _isSuffering = MutableLiveData<Boolean>()
    val isSuffering: LiveData<Boolean>
        get() = _isSuffering

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

    init {
        Timber.i("ResultViewModel created!")
        calculateResult()
    }

    private fun calculateResult() {
        var ratingSum = 0
        databaseHandler.questions.forEach { question ->
            if (question.itemQuestion)
                if (question.rating)
                    ratingSum++
        }
        Timber.i("Total rating sum: $ratingSum!")
        _resultGeneralHspContent.value = buildGeneralResultString(ratingSum)
        if (checkSuffering()) {
            Timber.i("User seems to suffer!")
            _isSuffering.value = true
            _resultConditionalContent.value =
                if (ratingSum < limitPositive) appRes.getString(R.string.detected_suffering_negative_hsp_message)
                else appRes.getString(R.string.detected_suffering_positive_hsp_message)
        }
        _isSuffering.value = false

        //TODO calculate which result texts from database should be shown onscreen
        //_resultContent = ...
    }

    /**
     * This function checks the rating of the questions that are meant to detect if the user is suffering.
     * If one of those three questions was answered positively, the return value is true.
     *
     * @return if the user is suffering
     * */
    private fun checkSuffering(): Boolean {
        if (questions.size <= 34) return false
        return questions[32].rating || questions[33].rating || questions[34].rating
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

    @SuppressLint("StringFormatMatches")
    fun buildGeneralResultString(rating: Int): String {
        var resultString = ""
        val isNegative = rating < limitPositive

        val probabilityArray = appRes.getStringArray(R.array.general_HSP_result_probability_array)
        val resultPositivity =
            if (rating > limitPositive * 1.5 || rating < limitPositive * 0.5)
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
        return resultString
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }
}