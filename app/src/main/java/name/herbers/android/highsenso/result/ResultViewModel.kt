package name.herbers.android.highsenso.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import name.herbers.android.highsenso.database.QuestionDatabaseDao
import timber.log.Timber

class ResultViewModel(
    val database: QuestionDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    //needed for coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        Timber.i("ResultViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }

}