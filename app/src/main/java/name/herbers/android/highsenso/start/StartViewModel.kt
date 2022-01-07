package name.herbers.android.highsenso.start

import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.Constants
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * [StartViewModel] for the [StartFragment].
 * Handles all non-UI tasks for StartFragment.
 *
 * This includes the handler for the reset questions button.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class StartViewModel(
    val databaseHandler: DatabaseHandler
) : ViewModel() {

    init {
        Timber.i("StartViewModel created!")
    }

    /**
     * Checks if there is an available [AnswerSheet] for the baseline questionnaire.
     * */
    fun baselineAnswerSheetAvailable(answerSheets: List<AnswerSheet>?): Boolean {
        var available = false
        if (answerSheets == null) return available
        for (answerSheet in answerSheets) {
            available = available || answerSheet.id == Constants.BASELINE_QUESTIONNAIRE_ID
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }
}