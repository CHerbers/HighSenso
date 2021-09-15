package name.herbers.android.highsenso.questioning

import androidx.lifecycle.ViewModel
import timber.log.Timber

class QuestioningViewModel: ViewModel() {

    init {
        Timber.i("QuestioningViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("QuestioningViewModel destroyed!")
    }
}