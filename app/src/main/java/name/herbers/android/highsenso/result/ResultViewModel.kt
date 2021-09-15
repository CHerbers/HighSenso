package name.herbers.android.highsenso.result

import androidx.lifecycle.ViewModel
import timber.log.Timber

class ResultViewModel: ViewModel() {

    init {
        Timber.i("ResultViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ResultViewModel destroyed!")
    }

}