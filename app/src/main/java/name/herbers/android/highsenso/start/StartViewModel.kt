package name.herbers.android.highsenso.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class StartViewModel : ViewModel() {

    private var _someText = MutableLiveData<String>()
    val someText: LiveData<String>
        get() = _someText

    init {
        _someText.value = "some text"
        Timber.i("StartViewModel created! With ${someText.value}")
    }

    //TODO delete
    //just to test click listener
    fun doStuff() {
        Timber.d("doStuff was called!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("StartViewModel destroyed!")
    }

}