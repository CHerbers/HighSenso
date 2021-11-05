package name.herbers.android.highsenso.questioning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PersonalQuestioningViewModel(
    private val databaseHandler: DatabaseHandler
) : ViewModel()  {

    /* observed by PersonalQuestioningFragment, if true: navigation to ResultFragment */
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    init {
        Timber.i("PersonalQuestioningViewModel created!")
    }
}