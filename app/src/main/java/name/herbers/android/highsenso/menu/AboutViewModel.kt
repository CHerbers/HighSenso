package name.herbers.android.highsenso.menu

import androidx.lifecycle.ViewModel
import timber.log.Timber

class AboutViewModel: ViewModel() {

    init {
        Timber.i("AboutViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("AboutViewModel destroyed!")
    }
}