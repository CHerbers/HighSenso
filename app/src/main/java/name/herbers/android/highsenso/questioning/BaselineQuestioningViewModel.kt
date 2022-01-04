package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * This is the [AndroidViewModel] of the [BaselineQuestioningFragment]. It holds every non UI-based
 * logic needed in the Fragment. This includes validity checks for user input, selection of error
 * messages and initialization of data sending.
 *
 *@project HighSenso
 *@author Herbers
 */
class BaselineQuestioningViewModel(
    private val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

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
    private val regexNumbers = "[0-9]".toRegex()
    private val regexLetters = "[a-zA-ZäöüÄÖÜß]".toRegex()

    init {
        Timber.i("PersonalQuestioningViewModel created!")
    }

    fun handleNextButtonClick() {
        //TODO: send data after checking privacy settings (but only one time!)
        _isFinished.value = true
        _isFinished.value = false
    }

    fun handleBackButtonClick() {

    }

    /**
     * Calculates a fitting error message depending on the input String.
     * Regarding [maxAge], [minAge] and [regexNumbers].
     * @param age the users age
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getAgeErrorMessage(age: String): String {
        if (age == "") return ""
        if (!regexNumbers.containsMatchIn(age)) return errorInvalidInput
        if (age.toInt() > maxAge) return errorTooOld
        if (age.toInt() < minAge) return errorTooYoung
        return ""
    }

    /**
     * Calculates a fitting error message depending on the input String. Regarding [regexNumbers].
     * @param childCount the amount of children the user has
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getChildrenErrorMessage(childCount: String): String {
        if (childCount == "") return ""
        if (!regexNumbers.containsMatchIn(childCount)) return errorInvalidInput
        return ""
    }

    /**
     * Calculates a fitting error message depending on the input String. Regarding [regexLetters].
     * @param profession the users profession
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getProfessionErrorMessage(profession: String): String {
        if (profession == "") return ""
        if (!regexLetters.containsMatchIn(profession)) return errorInvalidInput
        return ""
    }

}