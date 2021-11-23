package name.herbers.android.highsenso.result

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

    //some constants
    private val appRes = application.applicationContext.resources
    private val errorInvalidInput =
        appRes.getString(R.string.send_dialog_text_edit_error_invalid)
    private val errorTooOld =
        appRes.getString(R.string.send_dialog_text_edit_error_old)
    private val errorTooYoung =
        appRes.getString(R.string.send_dialog_text_edit_error_young)
    private val maxAge = appRes.getInteger(R.integer.max_age)
    private val minAge = appRes.getInteger(R.integer.min_age)
    private val regex = "[0-9]".toRegex()

    private val _resultTypeContent = MutableLiveData<String>()
    val resultTypeContent: LiveData<String>
        get() = _resultTypeContent

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
        Timber.i("Total rating sum: $ratingSum")
        //TODO calculate which result texts from database should be shown onscreen
        //_resultContent = ...
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

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }
}