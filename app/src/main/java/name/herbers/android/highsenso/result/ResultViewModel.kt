package name.herbers.android.highsenso.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

class ResultViewModel(
    val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

    private val _resultContent = MutableLiveData<String>()
    val resultContent: LiveData<String>
        get() = _resultContent


    init {
        Timber.i("ResultViewModel created!")
        calculateResult()
    }

    private fun calculateResult() {
        //TODO calculate which result texts from database should be shown onscreen
        //_resultContent = ...
    }

    fun handleSendResult() {
        //TODO send the stuff
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }

}