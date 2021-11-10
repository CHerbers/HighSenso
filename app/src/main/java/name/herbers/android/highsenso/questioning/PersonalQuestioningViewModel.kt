package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PersonalQuestioningViewModel(
    private val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application)  {

    /* observed by PersonalQuestioningFragment, if true: navigation to ResultFragment */
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

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

    init {
        Timber.i("PersonalQuestioningViewModel created!")
    }

    fun handleNextButtonClick() {
        _isFinished.value = true
        _isFinished.value = false
    }

    fun handleBackButtonClick() {

    }

    /**
     * Calculates a fitting error message depending on the input String.
     * @param age the users age
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getAgeErrorMessage(age: String): String {
        if (age == "") return ""
        if (!regex.containsMatchIn(age)) return errorInvalidInput
        if (age.toInt() > maxAge) return errorTooOld
        if (age.toInt() < minAge) return errorTooYoung
        return ""
    }

    fun getChildrenErrorMessage(childCount: String): String {
        return ""
    }

    fun getProfessionErrorMessage(profession: String): String {
        return ""
    }

}