package name.herbers.android.highsenso

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.PersonalData
import timber.log.Timber

/**
 * This [AndroidViewModel] belongs to the [MainActivity] and can therefore be accessed by
 * every Fragment of this App under this activity.
 * It is used to provide a [DatabaseHandler] and [PersonalData] to the Fragments and their
 * corresponding ViewModels.
 * The [databaseHandler] is used to handle every database access/manipulation.
 * The [personalData] stores user data that is not permanently stored on the device while the App
 * is alive.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModel(
    val databaseHandler: DatabaseHandler,
    val personalData: PersonalData
) : ViewModel() {
    var backFromResult: Boolean = false

    private val _gatherSensorData = MutableLiveData(false)
    val gatherSensorData: LiveData<Boolean>
        get() = _gatherSensorData

    private val _locationDialogDismiss = MutableLiveData(false)
    val locationDialogDismiss: LiveData<Boolean>
        get() = _locationDialogDismiss

    private val _startRegisterDialog = MutableLiveData(false)
    val startRegisterDialog: LiveData<Boolean>
        get() = _startRegisterDialog

    private val _startLoginDialog = MutableLiveData(false)
    val startLoginDialog: LiveData<Boolean>
        get() = _startLoginDialog

    private val _errorSendingData = MutableLiveData("")
    val errorSendingData: LiveData<String>
        get() = _errorSendingData

    private val _serverLoginResponse = MutableLiveData(-1)
    val serverLoginResponse: LiveData<Int>
        get() = _serverLoginResponse

    init {
        Timber.i("SharedViewModel created!")
    }

    /**
     * Sets the observed [locationDialogDismiss] to true to initiate a change from
     * [name.herbers.android.highsenso.start.StartFragment] to
     * [name.herbers.android.highsenso.questioning.QuestioningFragment].
     * This is done after the [name.herbers.android.highsenso.dialogs.LocationDialogFragment] is
     * dismissed.
     * */
    fun dialogGetsDismissed() {
        _locationDialogDismiss.value = true
        _locationDialogDismiss.value = false
    }

    fun sendLogin(username: String, password: String) {
        //TODO hash password
        // call handler
    }

    fun handleRegisterButtonClick() {
        _startRegisterDialog.value = true
        _startRegisterDialog.value = false
    }

    fun handleForgotPasswordClick() {
        //TODO reset password
    }

    fun handleLoginButtonClick() {
        _startLoginDialog.value = true
        _startLoginDialog.value = false
    }

    fun startGatherSensorData(){
        _gatherSensorData.value = true
    }

    fun stopGatherSensorData(){
        _gatherSensorData.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}