package name.herbers.android.highsenso.start

import androidx.lifecycle.ViewModel
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

    companion object{
        private const val BASELINE_ID = 1
    }

    init {
        Timber.i("StartViewModel created!")
    }

    /**
     * Checks if the profile data is complete.
     *
     * */
    fun profileDataAvailable(answerSheets: List<AnswerSheet>?): Boolean {
        var available = false
        if (answerSheets == null) return available
        for (answerSheet in answerSheets){
            available = answerSheet.id == BASELINE_ID
        }
        return available
    }

    /**
     * Rating of all questions in the database handled by [DatabaseHandler] are updated to the
     * default (unrated) rating (-1).
     * After changing rating [_resetDone] is set to true to trigger its observer in [StartFragment].
     * */
    //TODO delete this
    fun resetAllQuestionRatings(){
        //set questions rating to default value (-1)
        databaseHandler.questions.forEach { question ->
            question.rating = false
            databaseHandler.updateDatabase(question)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }
}