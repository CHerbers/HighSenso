package name.herbers.android.highsenso.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDialogViewModel : ViewModel() {
    private val _positiveAnswer = MutableLiveData(false)
    val positiveAnswer: LiveData<Boolean>
        get() = _positiveAnswer

    /**
     * If called [_positiveAnswer] is set to true in order to trigger any observer.
     * */
    fun answeredPositive() {
        _positiveAnswer.value = true
    }
}