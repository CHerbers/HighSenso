package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import name.herbers.android.highsenso.R
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
    application: Application
) : AndroidViewModel(application) {
    /* as long as both of those are false, the next button will show an error toast because there
    * are either empty EditTexts or EditTexts with invalid input */
    var validInputChildren: Boolean = false
    var validInputProfession: Boolean = false

    //some constants
    private val appRes = application.applicationContext.resources
    private val errorInvalidInput =
        appRes.getString(R.string.send_dialog_text_edit_error_invalid)
    private val regexNumbers = "[0-9]".toRegex()
    private val regexLetters = "[a-zA-ZäöüÄÖÜß]".toRegex()

    init {
        Timber.i("PersonalQuestioningViewModel created!")
    }

    /**
     * Calculates a fitting error message depending on the input String. Regarding [regexNumbers].
     * @param childCount the amount of children the user has
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getChildrenErrorMessage(childCount: String): String {
        validInputChildren = false
        if (childCount == "") return ""
        if (!regexNumbers.containsMatchIn(childCount)) return errorInvalidInput
        if (childCount.toInt() > 20) return errorInvalidInput
        validInputChildren =  true
        return ""
    }

    /**
     * Calculates a fitting error message depending on the input String. Regarding [regexLetters].
     * @param profession the users profession
     * @return a fitting error message if there is one or an empty string otherwise
     * */
    fun getProfessionErrorMessage(profession: String): String {
        validInputProfession = false
        if (profession == "")return ""
        if (!regexLetters.containsMatchIn(profession)) return errorInvalidInput
        validInputProfession = true
        return ""
    }

    fun validInput(): Boolean {
        return validInputProfession && validInputChildren
    }

}