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

    fun handleSendResult(age: String, gender: String) {
        //TODO send the stuff
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }

    fun checkSendResultInput(age: String): Boolean {

        val regex = "[0-9]".toRegex()
        if (regex.containsMatchIn(age) && age.toInt() < 150 && age.toInt() > 14) {
            Timber.i("$age is a valid age!")
            return true
        }
        Timber.i("$age is an invalid age!")
        return false
    }

}