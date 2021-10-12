package name.herbers.android.highsenso.start

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * [StartViewModel] for the [StartFragment].
 * Handles all non-UI tasks for StartFragment.
 * */
class StartViewModel(
    val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

    init {
        Timber.i("StartViewModel created!")
    }

    /**
     * Rating of all questions in the database handled by [DatabaseHandler] are updated to the
     * default (unrated) rating (stored in [R.integer.default_unrated_rating]).
     * After changing rating a [Toast] is shown with a corresponding message (stored in
     * [R.string.reset_dialog_toast_message]).
     * */
    fun handleResetQuestions(): Boolean {
        //set questions rating to default value (-1)
        databaseHandler.questions.forEach { question ->
            question.rating =
                getApplication<Application>().applicationContext.resources.getInteger(R.integer.default_unrated_rating)
            databaseHandler.updateDatabase(question)
        }
        //Toast message saying that reset is done
        Toast.makeText(
            getApplication<Application>().applicationContext,
            R.string.reset_dialog_toast_message,
            Toast.LENGTH_SHORT
        ).show()
        return true
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }
}