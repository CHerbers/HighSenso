package name.herbers.android.highsenso.menu

import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyViewModel: ViewModel() {



    init {
        Timber.i("PrivacyViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("PrivacyViewModel destroyed!")
    }
}