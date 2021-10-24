package name.herbers.android.highsenso.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val _resetDone = MutableLiveData(false)
    val resetDone: LiveData<Boolean>
        get() = _resetDone

    init {
        Timber.i("StartViewModel created!")
    }

    /**
     * Rating of all questions in the database handled by [DatabaseHandler] are updated to the
     * default (unrated) rating (-1).
     * After changing rating [_resetDone] is set to true to trigger its observer in [StartFragment].
     * */
    fun handleResetQuestions() {
        //set questions rating to default value (-1)
        databaseHandler.questions.forEach { question ->
            question.rating = -1
            databaseHandler.updateDatabase(question)
        }
        //trigger Toast message on StartFragment
        _resetDone.value = true
        _resetDone.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }
}