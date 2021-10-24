package name.herbers.android.highsenso.menu

import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 * This is the [ViewModel] for [AboutFragment].
 * Every non-UI task needed in the AboutFragment is done in this class.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class AboutViewModel: ViewModel() {

    init {
        Timber.i("AboutViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("AboutViewModel destroyed!")
    }
}