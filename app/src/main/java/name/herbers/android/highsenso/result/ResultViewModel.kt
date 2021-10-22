package name.herbers.android.highsenso.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

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
    private val maxAge = appRes.getInteger(R.integer.send_result_max_age)
    private val minAge = appRes.getInteger(R.integer.send_result_min_age)
    private val regex = "[0-9]".toRegex()

    private val _resultContent = MutableLiveData<String>()
    val resultContent: LiveData<String>
        get() = _resultContent


    init {
        Timber.i("ResultViewModel created!")
        calculateResult()
    }

    private fun calculateResult() {
        val ratingSum = databaseHandler.questions.sumOf { question ->
            question.rating + 1
        }
        Timber.i("Total rating sum: $ratingSum")
        //TODO calculate which result texts from database should be shown onscreen
        //_resultContent = ...
    }

    fun handleSendResult(age: Int, gender: String) {
        //TODO send the stuff
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