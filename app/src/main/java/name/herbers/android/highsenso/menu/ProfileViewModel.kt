package name.herbers.android.highsenso.menu

import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class ProfileViewModel: ViewModel() {
    init {
        Timber.i("ProfileViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ProfileViewModel destroyed!")
    }
}