package name.herbers.android.highsenso.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class StartViewModel : ViewModel() {

    private var _someText = MutableLiveData<String>()
    val someText: LiveData<String>
        get() = _someText


    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }

}