package name.herbers.android.highsenso.start

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

class SharedViewModel(
    val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        Timber.i("StartViewModel created!")
    }

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